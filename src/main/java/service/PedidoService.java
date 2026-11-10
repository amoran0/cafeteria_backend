package service;

import dtos.PedidoRequestDto;
import dtos.PedidoResponseDto;
import exceptions.NotFoundException;
import exceptions.ReglaNegocioException;
import integracion.FitZoneClient;
import lombok.RequiredArgsConstructor;
import modelos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositorios.IClienteRepositorio;
import repositorios.IPedidoRepositorio;
import repositorios.IProductoRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private static final BigDecimal DESCUENTO_SOCIO = new BigDecimal("0.10");
    private static final BigDecimal DIVISOR_IVA = new BigDecimal("1.10");

    private final IPedidoRepositorio pedidoRepositorio;
    private final IClienteRepositorio clienteRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final CuponService cuponService;
    private final FitZoneClient fitZoneClient;

    @Transactional
    public PedidoResponseDto crear(PedidoRequestDto dto) {
        Cliente cliente = clienteRepositorio.findById(dto.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        // RN-04: agrupar líneas del mismo producto y comprobar límites
        Map<UUID, Integer> cantidades = new LinkedHashMap<>();
        dto.lineas().forEach(l -> cantidades.merge(l.productoId(), l.cantidad(), Integer::sum));
        if (cantidades.isEmpty() || cantidades.size() > 15) {
            throw new ReglaNegocioException("Un pedido debe tener entre 1 y 15 líneas");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<UUID, Integer> entrada : cantidades.entrySet()) {
            int cantidad = entrada.getValue();
            Producto producto = productoRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + entrada.getKey()));
            if (cantidad < 1 || cantidad > 20) {
                throw new ReglaNegocioException("La línea de '" + producto.getNombre() + "' debe tener entre 1 y 20 unidades");
            }
            // RN-01 y RN-02
            if (!Boolean.TRUE.equals(producto.getActiva()) || !Boolean.TRUE.equals(producto.getDisponible())) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' no está disponible");
            }
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
            cupon = cuponService.buscarValido(dto.codigoCupon(), subtotal, cliente.getMail());
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

        return PedidoResponseDto.of(pedidoRepositorio.save(pedido));
    }

    @Transactional(readOnly = true)
    public PedidoResponseDto obtener(UUID id) {
        return PedidoResponseDto.of(buscar(id));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDto> listar(UUID clienteId, EstadoPedido estado, LocalDateTime desde,
                                          LocalDateTime hasta, Pageable pageable) {
        // sin filtro = todos los estados y un rango de fechas amplio (Postgres no admite parámetros nulos aquí)
        List<EstadoPedido> estados = estado == null ? List.of(EstadoPedido.values()) : List.of(estado);
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde;
        LocalDateTime fin = hasta == null ? LocalDateTime.of(3000, 1, 1, 0, 0) : hasta;
        Page<Pedido> pedidos = clienteId != null
                ? pedidoRepositorio.buscarPedidosCliente(clienteId, estados, inicio, fin, pageable)
                : pedidoRepositorio.buscarPedidos(estados, inicio, fin, pageable);
        return pedidos.map(PedidoResponseDto::of);
    }

    /** RN-08 */
    @Transactional
    public PedidoResponseDto cambiarEstado(UUID id, EstadoPedido nuevo) {
        Pedido pedido = buscar(id);
        if (!pedido.getEstado().puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException("No se puede pasar de " + pedido.getEstado() + " a " + nuevo);
        }
        pedido.setEstado(nuevo); // al cancelar, el uso del cupón se libera: solo cuentan pedidos no cancelados
        return PedidoResponseDto.of(pedido);
    }

    private Pedido buscar(UUID id) {
        return pedidoRepositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));
    }
}
