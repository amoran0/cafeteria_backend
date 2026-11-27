package es.safareyes.cafeteria.repositorios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.safareyes.cafeteria.dtos.response.VentasPorDiaDto;
import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepositorio extends JpaRepository<Pedido, UUID> {

    /**
     * Q8 (endpoint 10, GET /api/v1/pedidos/{id}): trae el pedido con sus líneas, el producto de cada línea y el cupón
     * en una sola consulta con JOIN, en vez de 1 consulta para las líneas + 1 por cada producto (problema N+1).
     */
    @Override
    @EntityGraph(attributePaths = {"lineas", "lineas.producto", "cupon"})
    Optional<Pedido> findById(UUID id);

    @Query("""
        SELECT p FROM Pedido p
        WHERE p.estado IN :estados AND p.fecha >= :desde AND p.fecha < :hasta
    """)
    Page<Pedido> buscarPedidos(@Param("estados") List<EstadoPedido> estados,
                               @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta,
                               Pageable pageable);

    @Query("""
        SELECT p FROM Pedido p
        WHERE p.cliente.idCliente = :clienteId
          AND p.estado IN :estados AND p.fecha >= :desde AND p.fecha < :hasta
    """)
    Page<Pedido> buscarPedidosCliente(@Param("clienteId") UUID clienteId,
                                      @Param("estados") List<EstadoPedido> estados,
                                      @Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta,
                                      Pageable pageable);

    /** RN-09: pedidos creados desde el inicio del día */
    long countByFechaGreaterThanEqual(LocalDateTime inicioDia);

    /** RN-06: usos de un cupón (los pedidos cancelados no cuentan, RN-08) */
    long countByCupon_IdCuponAndEstadoNot(UUID idCupon, EstadoPedido estado);

    /**
     * RF-07 / RN-10 (Q3): ventas de un día con SQL nativo. Solo cuentan los pedidos ENTREGADO.
     * DATE_TRUNC('day', fecha) quita la hora para comparar con el día pedido y COALESCE devuelve 0
     * en lugar de null cuando ese día no hay ventas. Los alias van entre comillas para que PostgreSQL
     * respete las mayúsculas y coincidan con los getters de VentasPorDiaDto.
     */
    @Query(value = """
        SELECT COUNT(*)                          AS "numeroPedidos",
               COALESCE(SUM(total), 0)           AS "total",
               COALESCE(SUM(base_imponible), 0)  AS "base",
               COALESCE(SUM(cuota_iva), 0)       AS "iva"
        FROM pedido
        WHERE estado = 'ENTREGADO'
          AND DATE_TRUNC('day', fecha) = :fecha
    """, nativeQuery = true)
    VentasPorDiaDto obtenerVentasDia(@Param("fecha") LocalDate fecha);
}
