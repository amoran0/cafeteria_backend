package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.dtos.request.PedidoRequestDto;
import es.safareyes.cafeteria.dtos.response.PedidoResponseDto;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import es.safareyes.cafeteria.integracion.FitZoneClient;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.conversores.PedidoMapper;
import es.safareyes.cafeteria.modelos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.cafeteria.repositorios.IClienteRepositorio;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PedidoService {
    private static final BigDecimal DESCUENTO_SOCIO = new BigDecimal("0.10");
    private static final BigDecimal DIVISOR_IVA = new BigDecimal("1.10");

    private IPedidoRepositorio pedidoRepositorio;
    private IClienteRepositorio clienteRepositorio;
    private IProductoRepositorio productoRepositorio;
    private CuponService cuponService;
    private FitZoneClient fitZoneClient;
    private PedidoMapper mapper;

    @Transactional
    public PedidoResponseDto crear(PedidoRequestDto dto, Usuario usuario) {
        Cliente cliente = clienteDelPedido(dto.clienteId(), usuario);

        // RN-04: cada línea recibida debe tener entre 1 y 20 unidades antes de agrupar,
        // para que una cantidad negativa o 0 no se "compense" con otra línea del mismo producto
        for (int i = 0; i < dto.lineas().size(); i++) {
            int cantidad = dto.lineas().get(i).cantidad();
            if (cantidad < 1 || cantidad > 20) {
                throw new ReglaNegocioException("La línea " + (i + 1) + " tiene " + cantidad
                        + " unidades y cada línea debe tener entre 1 y 20 (RN-04)");
            }
        }

        // RN-04: agrupar líneas del mismo producto y comprobar límites
        Map<UUID, Integer> cantidades = new LinkedHashMap<>();
        dto.lineas().forEach(l -> cantidades.merge(l.productoId(), l.cantidad(), Integer::sum));
        if (cantidades.isEmpty() || cantidades.size() > 15) {
            throw new ReglaNegocioException("Un pedido debe tener entre 1 y 15 líneas (productos distintos) y se han recibido "
                    + cantidades.size() + " (RN-04)");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());   // el estado se crea como PENDIENTE

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<UUID, Integer> entrada : cantidades.entrySet()) {
            int cantidad = entrada.getValue();
            Producto producto = productoRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún producto con id " + entrada.getKey()));
            if (cantidad > 20) {   // cada línea ya tiene al menos 1: aquí solo puede pasarse de 20 al sumar
                throw new ReglaNegocioException("La línea del producto '" + producto.getNombre() + "' tiene " + cantidad
                        + " unidades y debe tener entre 1 y 20 (si el producto se repite, se suman sus cantidades) (RN-04)");
            }
            comprobarSePuedePedir(producto);
            LineaPedido linea = new LineaPedido();
            linea.setPedido(pedido);
            linea.setProducto(producto);
            linea.setCantidad(cantidad);
            linea.setPrecioUnitario(producto.getPrecioVenta()); // RN-03
            pedido.getLineas().add(linea);
            subtotal = subtotal.add(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
        }

        // RN-07: cupón o socio FitZone, se aplica el mayor (no se acumulan)
        BigDecimal descuento = BigDecimal.ZERO;
        Cupon cupon = null;
        if (dto.codigoCupon() != null && !dto.codigoCupon().isBlank()) {
            // sin cliente no hay email: un cupón personal no será válido
            cupon = cuponService.buscarValido(dto.codigoCupon(), subtotal, cliente == null ? null : cliente.getMail());
            descuento = cuponService.calcularDescuento(cupon, subtotal);
        }
        if (dto.dniSocio() != null && !dto.dniSocio().isBlank() && fitZoneClient.esSocioActivo(dto.dniSocio())) {
            BigDecimal descuentoSocio = subtotal.multiply(DESCUENTO_SOCIO).setScale(2, RoundingMode.HALF_UP);
            if (descuentoSocio.compareTo(descuento) > 0) {
                descuento = descuentoSocio;
                cupon = null;
            }
        }
        pedido.setCupon(cupon);

        // RN-05: el total nunca es negativo; base = total / 1,10; todo a 2 decimales
        descuento = descuento.min(subtotal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal base = total.divide(DIVISOR_IVA, 2, RoundingMode.HALF_UP);
        pedido.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        pedido.setDescuento(descuento);
        pedido.setTotal(total);
        pedido.setBaseImponible(base);
        pedido.setCuotaIva(total.subtract(base));

        // RN-09: número de pedidos creados hoy más uno
        LocalDateTime inicioDia = pedido.getFecha().toLocalDate().atStartOfDay();
        pedido.setNumeroTurno((int) pedidoRepositorio.countByFechaGreaterThanEqual(inicioDia) + 1);

        return mapper.toDto(pedidoRepositorio.save(pedido));
    }

    /** Un CLIENTE solo puede ver sus propios pedidos: si no es suyo → 403 */
    @Transactional(readOnly = true)
    public PedidoResponseDto obtener(UUID id, Usuario usuario) {
        Pedido pedido = buscar(id);
        UUID propio = clientePropio(usuario);
        if (propio != null && (pedido.getCliente() == null || !propio.equals(pedido.getCliente().getIdCliente()))) {
            throw new AccessDeniedException("El pedido " + id + " no es tuyo: un cliente solo puede consultar sus propios pedidos");
        }
        return mapper.toDto(pedido);
    }

    /** Un CLIENTE solo ve sus pedidos: el filtro de cliente se fuerza al suyo */
    @Transactional(readOnly = true)
    public Page<PedidoResponseDto> listar(UUID clienteId, EstadoPedido estado, LocalDateTime desde,
                                          LocalDateTime hasta, Pageable pageable, Usuario usuario) {
        // hasta llega ya como el inicio del día siguiente, así que hasta <= desde equivale a hasta < desde en fechas
        if (desde != null && hasta != null && !hasta.isAfter(desde)) {
            throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
        }
        UUID propio = clientePropio(usuario);
        if (propio != null) {
            if (clienteId != null && !clienteId.equals(propio)) {
                throw new AccessDeniedException("Un cliente solo puede consultar sus propios pedidos");
            }
            clienteId = propio;
        }
        // sin filtro = todos los estados y un rango de fechas amplio
        List<EstadoPedido> estados = estado == null ? List.of(EstadoPedido.values()) : List.of(estado);
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde;
        LocalDateTime fin = hasta == null ? LocalDateTime.of(3000, 1, 1, 0, 0) : hasta;
        Page<Pedido> pedidos = clienteId != null
                ? pedidoRepositorio.buscarPedidosCliente(clienteId, estados, inicio, fin, pageable)
                : pedidoRepositorio.buscarPedidos(estados, inicio, fin, pageable);
        return pedidos.map(mapper::toDto);
    }

    /** RN-08 */
    @Transactional
    public PedidoResponseDto cambiarEstado(UUID id, EstadoPedido nuevo) {
        Pedido pedido = buscar(id);
        if (!pedido.getEstado().puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("El pedido " + id + " no puede pasar de " + pedido.getEstado() + " a " + nuevo
                    + ". Transiciones válidas: PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO y PENDIENTE → CANCELADO (RN-08)");
        }
        pedido.setEstado(nuevo); // al cancelar, el uso del cupón se libera: solo cuentan pedidos no cancelados
        return mapper.toDto(pedido);
    }

    /**
     * Cliente al que se asigna el pedido (opcional, 5.5):
     * un CLIENTE siempre pide para su propia ficha; el personal puede indicar un cliente o ninguno (pedido de barra).
     */
    private Cliente clienteDelPedido(UUID clienteId, Usuario usuario) {
        UUID propio = clientePropio(usuario);
        if (propio != null && clienteId != null && !clienteId.equals(propio)) {
            throw new AccessDeniedException("Un cliente solo puede crear pedidos para sí mismo");
        }
        UUID id = propio != null ? propio : clienteId;
        if (id == null) {
            return null;   // pedido de barra sin cliente
        }
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún cliente con id " + id));
    }

    /**
     * Id de la ficha de cliente (se crea al registrarse) si el usuario con sesión es CLIENTE;
     * null si es personal (ADMIN o BARISTA), que puede ver y crear pedidos de cualquiera.
     */
    private UUID clientePropio(Usuario usuario) {
        if (usuario.getRol() != RolUsuario.CLIENTE) {
            return null;
        }
        return clienteRepositorio.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Cliente::getIdCliente)
                .orElseThrow(() -> new ReglaNegocioException("El usuario " + usuario.getMail() + " no tiene ficha de cliente"));
    }

    /** RN-01 y RN-02: solo se pueden pedir productos activos, disponibles y de una categoría disponible */
    private void comprobarSePuedePedir(Producto producto) {
        String nombre = "El producto '" + producto.getNombre() + "' (id " + producto.getIdProducto() + ")";
        if (!Boolean.TRUE.equals(producto.getActiva())) {
            throw new ReglaNegocioException(nombre + " está dado de baja y no se puede pedir (RN-02)");
        }
        if (!Boolean.TRUE.equals(producto.getCategoria().getDisponible())) {
            throw new ReglaNegocioException(nombre + " está agotado porque su categoría '"
                    + producto.getCategoria().getNombre() + "' está agotada y no se puede pedir (RN-02)");
        }
        if (!Boolean.TRUE.equals(producto.getDisponible())) {
            throw new ReglaNegocioException(nombre + " está agotado y no se puede pedir (RN-02)");
        }
    }

    private Pedido buscar(UUID id) {
        return pedidoRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún pedido con id " + id));
    }
}
