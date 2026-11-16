package es.safareyes.cafeteria;

import es.safareyes.cafeteria.dtos.CategoriasActivos;
import es.safareyes.cafeteria.dtos.ProducotsMasVendidos;
import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Pedido;
import es.safareyes.cafeteria.modelos.Producto;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CafeteriaApplicationTest {

    @Autowired
    private IProductoRepositorio productoRepositorio;

    @Autowired
    private ICategoriaRepositorio categoriaRepositorio;

    @Autowired
    private IPedidoRepositorio pedidoRepositorio;


    // GET /api/v1/categorias
    @Test
    void listarCategorias() {
        List<CategoriasActivos> todos = categoriaRepositorio.buscarCategoriasProductosActivos();
    }

    // GET /api/v1/productos
    @Test
    void listarCarta() {
        List<Producto> todos = productoRepositorio.findAll();
    }

    // GET /api/v1/productos/{id}
    @Test
    void obtenerProducto() {
        Optional<Producto> producto = productoRepositorio.findById(
                UUID.fromString("f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01"));
    }

    // GET /api/v1/pedidos/{id}
    @Test
    void obtenerPedido() {
        Optional<Pedido> pedido = pedidoRepositorio.findById(
                UUID.fromString("b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01"));
    }

    // GET /api/v1/pedidos
    @Test
    void listarPedidos() {
        Page<Pedido> pedidos = pedidoRepositorio.buscarPedidos(
                List.of(EstadoPedido.ENTREGADO, EstadoPedido.PENDIENTE),
                LocalDateTime.of(2026, 10, 4, 0, 0), LocalDateTime.of(2026, 10, 5, 0, 0), PageRequest.of(0, 10));
    }

    // GET /api/v1/pedidos?clienteId=...
    @Test
    void listarPedidosCliente() {
        Page<Pedido> pedidos = pedidoRepositorio.buscarPedidosCliente(
                UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01"), List.of(EstadoPedido.ENTREGADO),
                LocalDateTime.of(2026, 10, 4, 0, 0), LocalDateTime.of(2026, 10, 5, 0, 0), PageRequest.of(0, 10));
    }

    // GET /api/v1/informes/ventas-dia
    @Test
    void ventasDia() {
        List<Pedido> pedidos = pedidoRepositorio.findByEstadoAndFechaGreaterThanEqualAndFechaLessThan(
                EstadoPedido.ENTREGADO, LocalDateTime.of(2026, 10, 4, 0, 0), LocalDateTime.of(2026, 10, 5, 0, 0));
    }

    // GET /api/v1/informes/productos-top
    @Test
    void productosTop() {
        List<ProducotsMasVendidos> todos = productoRepositorio.obtenerProductosTop(EstadoPedido.ENTREGADO,
                LocalDateTime.of(2026, 10, 4, 0, 0), LocalDateTime.of(2026, 10, 5, 0, 0), PageRequest.of(0, 10));
    }
}
