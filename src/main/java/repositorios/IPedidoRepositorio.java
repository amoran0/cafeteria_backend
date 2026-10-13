package repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import dtos.VentasPorDia;
import modelos.EstadoPedido;
import modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepositorio extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p")
    List<Pedido> obtenerTodosPedidos();

    List<Pedido> findByCliente_IdClienteAndEstadoAndFechaBetween(
            UUID clienteId,
            EstadoPedido estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    @Query(nativeQuery = true, value = """
        SELECT
            COUNT(DISTINCT p.id_pedido) AS numeroPedidos,
            COALESCE(SUM(pp.cantidad * pp.precio_unitario), 0) AS total,
            COALESCE(
                SUM(
                    (pp.cantidad * pp.precio_unitario)
                    / (1 + COALESCE(pr.iva, 0) / 100.0)
                ), 0
            ) AS base,
            COALESCE(
                SUM(
                    pp.cantidad * pp.precio_unitario
                    -
                    (pp.cantidad * pp.precio_unitario)
                    / (1 + COALESCE(pr.iva, 0) / 100.0)
                ), 0
            ) AS iva
        FROM pedido p
        LEFT JOIN pedido_producto pp ON pp.id_pedido = p.id_pedido
        LEFT JOIN producto pr ON pr.id_producto = pp.id_producto
        WHERE p.fecha >= :fecha
          AND p.fecha < (:fecha + CAST('1 day' AS INTERVAL))
    """)
    List<VentasPorDia> obtenerEstadisticasPedidosPorDia(@Param("fecha") LocalDateTime fecha);
}