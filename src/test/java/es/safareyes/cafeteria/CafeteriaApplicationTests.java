package es.safareyes.cafeteria;

import es.safareyes.cafeteria.dtos.response.CategoriasActivos;
import es.safareyes.cafeteria.dtos.response.ProducotsMasVendidos;
import es.safareyes.cafeteria.dtos.response.ProductoCartaVista;
import es.safareyes.cafeteria.dtos.response.VentasPorDiaDto;
import es.safareyes.cafeteria.modelos.Cupon;
import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Pedido;
import es.safareyes.cafeteria.modelos.Producto;
import es.safareyes.cafeteria.modelos.RolUsuario;
import es.safareyes.cafeteria.modelos.Usuario;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IClienteRepositorio;
import es.safareyes.cafeteria.repositorios.ICuponRepositorio;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;
import es.safareyes.cafeteria.repositorios.IUsuarioRepositorio;
import es.safareyes.cafeteria.repositorios.ProductoSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprueba cada consulta de los repositorios contra la BD real (datos de db/cafeteria.sql).
 * Cada test va en una transacción que se deshace al terminar, así que el UPDATE de RF-03 no deja cambios.
 * Los pedidos del 04/10/2026 son fijos en el script: 10 pedidos, 6 ENTREGADO por 42,68 €.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CafeteriaApplicationTest {

    private static final UUID CAT_BOLLERIA = UUID.fromString("e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a05");
    private static final UUID ALERGENO_GLUTEN = UUID.fromString("a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01");
    private static final UUID PRODUCTO_ESPRESSO = UUID.fromString("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01");
    private static final UUID PRODUCTO_TE_ROJO = UUID.fromString("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31");
    private static final UUID PEDIDO_1 = UUID.fromString("b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01");
    private static final UUID CLIENTE_CARLOS = UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01");
    private static final UUID USUARIO_CARLOS = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14");

    private static final LocalDateTime DIA_4 = LocalDateTime.of(2026, 10, 4, 0, 0);
    private static final LocalDateTime DIA_5 = LocalDateTime.of(2026, 10, 5, 0, 0);
    private static final LocalDate FECHA_4 = LocalDate.of(2026, 10, 4);

    @Autowired
    private IProductoRepositorio productoRepositorio;

    @Autowired
    private ICategoriaRepositorio categoriaRepositorio;

    @Autowired
    private IPedidoRepositorio pedidoRepositorio;

    @Autowired
    private ICuponRepositorio cuponRepositorio;

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;

    @Autowired
    private IClienteRepositorio clienteRepositorio;

    // CATEGORÍAS

    // GET /api/v1/categorias: cada categoría con su número de productos activos
    @Test
    void listarCategoriasConProductosActivos() {
        List<CategoriasActivos> categorias = categoriaRepositorio.buscarCategoriasProductosActivos();

        assertThat(categorias).hasSize(10);
        assertThat(categorias)
                .filteredOn(c -> c.nombre().equals("Bollería Artesanal"))
                .singleElement()
                .satisfies(c -> assertThat(c.cantidadProductosActivos()).isEqualTo(6L));
    }

    // PRODUCTOS

    // Q7 · GET /api/v1/productos sin filtros: solo los productos activos (RN-01)
    @Test
    void cartaSinFiltrosSoloActivos() {
        Page<Producto> carta = productoRepositorio.findAll(
                ProductoSpecifications.carta(null, null, null, null, null), PageRequest.of(0, 10));

        assertThat(carta.getTotalElements()).isEqualTo(52);   // 53 en el script, 1 dado de baja
        assertThat(carta.getContent()).hasSize(10).allMatch(Producto::getActiva);
    }

    // Q7 · filtros combinables: categoría, texto, precio máximo y excluir un alérgeno
    @Test
    void cartaConFiltros() {
        assertThat(productoRepositorio.count(ProductoSpecifications.carta(CAT_BOLLERIA, null, null, null, null)))
                .isEqualTo(6);
        assertThat(productoRepositorio.count(ProductoSpecifications.carta(null, "CAFÉ", null, null, null)))
                .isEqualTo(6);
        assertThat(productoRepositorio.count(ProductoSpecifications.carta(null, null, null, null, ALERGENO_GLUTEN)))
                .isEqualTo(30);

        BigDecimal maximo = new BigDecimal("2.00");
        List<Producto> baratos = productoRepositorio.findAll(ProductoSpecifications.carta(null, null, maximo, null, null));
        assertThat(baratos).isNotEmpty().allMatch(p -> p.getPrecioVenta().compareTo(maximo) <= 0);
    }

    // Q5 · la carta devuelta con la proyección ProductoCartaView, paginada y ordenada por precio
    @Test
    void cartaConProyeccion() {
        Page<ProductoCartaVista> carta = productoRepositorio.findBy(
                ProductoSpecifications.carta(CAT_BOLLERIA, null, null, null, null),
                q -> q.as(ProductoCartaVista.class).page(PageRequest.of(0, 4, Sort.by("precioVenta"))));

        assertThat(carta.getTotalElements()).isEqualTo(6);
        assertThat(carta.getContent()).hasSize(4);
        ProductoCartaVista primero = carta.getContent().getFirst();
        assertThat(primero.getNombre()).isNotBlank();
        assertThat(primero.getCategoria().getNombre()).isEqualTo("Bollería Artesanal");
        assertThat(carta.getContent()).extracting(ProductoCartaVista::getPrecioVenta).isSorted();
    }

    // GET /api/v1/productos/{id}: detalle con categoría y alérgenos
    @Test
    void obtenerProducto() {
        Optional<Producto> producto = productoRepositorio.findById(PRODUCTO_ESPRESSO);

        assertThat(producto).isPresent();
        assertThat(producto.get().getNombre()).isEqualTo("Café Espresso");
        assertThat(producto.get().getPrecioVenta()).isEqualByComparingTo("1.50");
        assertThat(producto.get().getCategoria().getNombre()).isEqualTo("Cafés Clásicos");
        assertThat(producto.get().getAlergenos()).isEmpty();
    }

    // Q1 · RN-01: solo se encuentra el producto si está activo (el té rojo está dado de baja en el script)
    @Test
    void obtenerProductoActivo() {
        assertThat(productoRepositorio.findByIdProductoAndActivaTrue(PRODUCTO_ESPRESSO)).isPresent();
        assertThat(productoRepositorio.findByIdProductoAndActivaTrue(PRODUCTO_TE_ROJO)).isEmpty();
    }

    // Q4 · PATCH /api/v1/categorias/{id}/disponibilidad: agota toda la categoría de una vez
    @Test
    void agotarCategoria() {
        int modificados = productoRepositorio.cambiarDisponibilidadCategoria(CAT_BOLLERIA, false);

        assertThat(modificados).isEqualTo(6);
        assertThat(productoRepositorio.count(ProductoSpecifications.carta(CAT_BOLLERIA, null, null, true, null)))
                .isZero();
    }

    // Q3 · GET /api/v1/informes/productos-top: consulta nativa con DATE_TRUNC y LIMIT, solo pedidos ENTREGADO
    @Test
    void productosTop() {
        List<ProducotsMasVendidos> top = productoRepositorio.obtenerProductosTop(FECHA_4, FECHA_4, 5);

        assertThat(top).hasSize(5);
        assertThat(top.getFirst().getNombre()).isEqualTo("Tostada de Aguacate y Huevo");
        assertThat(top.getFirst().getUnidadesVendidas()).isEqualTo(3L);
        assertThat(top.getFirst().getTotalVendido()).isEqualByComparingTo("14.70");
        assertThat(top).extracting(ProducotsMasVendidos::getUnidadesVendidas).isSortedAccordingTo((a, b) -> b.compareTo(a));
        assertThat(productoRepositorio.obtenerProductosTop(FECHA_4, FECHA_4, 2)).hasSize(2);
    }

    // PEDIDOS

    // Q8 · GET /api/v1/pedidos/{id}: pedido con sus líneas y productos
    @Test
    void obtenerPedido() {
        Optional<Pedido> pedido = pedidoRepositorio.findById(PEDIDO_1);

        assertThat(pedido).isPresent();
        assertThat(pedido.get().getNumeroTurno()).isEqualTo(1);
        assertThat(pedido.get().getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
        assertThat(pedido.get().getTotal()).isEqualByComparingTo("3.65");
        assertThat(pedido.get().getLineas()).hasSize(2)
                .allSatisfy(l -> assertThat(l.getProducto().getNombre()).isNotBlank());
    }

    // Q6 · GET /api/v1/pedidos?desde=&hasta=&estado=
    @Test
    void listarPedidos() {
        Page<Pedido> todos = pedidoRepositorio.buscarPedidos(
                List.of(EstadoPedido.values()), DIA_4, DIA_5, PageRequest.of(0, 4));
        Page<Pedido> entregados = pedidoRepositorio.buscarPedidos(
                List.of(EstadoPedido.ENTREGADO), DIA_4, DIA_5, PageRequest.of(0, 10));

        assertThat(todos.getTotalElements()).isEqualTo(10);
        assertThat(todos.getContent()).hasSize(4);
        assertThat(todos.getTotalPages()).isEqualTo(3);
        assertThat(entregados.getTotalElements()).isEqualTo(6);
        assertThat(entregados.getContent()).allMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO);
    }

    // GET /api/v1/pedidos con un CLIENTE: solo sus pedidos
    @Test
    void listarPedidosCliente() {
        Page<Pedido> pedidos = pedidoRepositorio.buscarPedidosCliente(
                CLIENTE_CARLOS, List.of(EstadoPedido.ENTREGADO), DIA_4, DIA_5, PageRequest.of(0, 10));

        assertThat(pedidos.getTotalElements()).isEqualTo(1);
        assertThat(pedidos.getContent().getFirst().getCliente().getIdCliente()).isEqualTo(CLIENTE_CARLOS);
    }

    // RN-09: pedidos creados desde una fecha (el 04/10/2026 se crearon 10)
    @Test
    void contarPedidosDelDia() {
        long desdeDia4 = pedidoRepositorio.countByFechaGreaterThanEqual(DIA_4);
        long desdeDia5 = pedidoRepositorio.countByFechaGreaterThanEqual(DIA_5);

        assertThat(desdeDia4 - desdeDia5).isEqualTo(10);
    }

    // Q3 · RN-10 · GET /api/v1/informes/ventas-dia: nativa con DATE_TRUNC y COALESCE, solo los ENTREGADO del día
    @Test
    void ventasDia() {
        VentasPorDiaDto ventas = pedidoRepositorio.obtenerVentasDia(FECHA_4);

        assertThat(ventas.getNumeroPedidos()).isEqualTo(6L);
        assertThat(ventas.getTotal()).isEqualByComparingTo("42.68");
        assertThat(ventas.getBase().add(ventas.getIva())).isEqualByComparingTo(ventas.getTotal());

        // un día sin ventas: COALESCE devuelve 0 en lugar de null
        VentasPorDiaDto sinVentas = pedidoRepositorio.obtenerVentasDia(LocalDate.of(2000, 1, 1));
        assertThat(sinVentas.getNumeroPedidos()).isZero();
        assertThat(sinVentas.getTotal()).isEqualByComparingTo("0");
    }

    // CUPONES

    // RN-06: búsqueda y existencia por código sin distinguir mayúsculas; usos sin contar cancelados
    @Test
    void cupones() {
        Optional<Cupon> cupon = cuponRepositorio.findByCodigoIgnoreCase("bienvenidacafe");

        assertThat(cupon).isPresent();
        assertThat(cupon.get().getCodigo()).isEqualTo("BIENVENIDACAFE");
        assertThat(cuponRepositorio.existsByCodigoIgnoreCase("Dulce20")).isTrue();
        assertThat(cuponRepositorio.existsByCodigoIgnoreCase("NOEXISTE")).isFalse();
        assertThat(pedidoRepositorio.countByCupon_IdCuponAndEstadoNot(cupon.get().getIdCupon(), EstadoPedido.CANCELADO))
                .isEqualTo(2);
    }

    // USUARIOS

    // Login y registro: el mail es el nombre de usuario y no distingue mayúsculas
    @Test
    void usuarios() {
        Optional<Usuario> admin = usuarioRepositorio.findByMailIgnoreCase("GERENCIA@cafeteria.com");

        assertThat(admin).isPresent();
        assertThat(admin.get().getRol()).isEqualTo(RolUsuario.ADMIN);
        assertThat(usuarioRepositorio.existsByMailIgnoreCase("Carlos.Gomez@gmail.com")).isTrue();
        assertThat(usuarioRepositorio.existsByMailIgnoreCase("nadie@cafeteria.com")).isFalse();
    }

    // Ficha de cliente del usuario con sesión (pedidos propios)
    @Test
    void clienteDeUnUsuario() {
        assertThat(clienteRepositorio.findByUsuario_IdUsuario(USUARIO_CARLOS))
                .isPresent()
                .get()
                .satisfies(c -> assertThat(c.getIdCliente()).isEqualTo(CLIENTE_CARLOS));
    }
}
