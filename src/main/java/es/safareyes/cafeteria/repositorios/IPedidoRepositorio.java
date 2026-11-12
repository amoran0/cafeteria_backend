package es.safareyes.cafeteria.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepositorio extends JpaRepository<Pedido, UUID> {

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

    @Query("SELECT p FROM Pedido p")
    List<Pedido> obtenerTodosPedidos();

    List<Pedido> findByCliente_IdClienteAndEstadoAndFechaBetween(
            UUID clienteId,
            EstadoPedido estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /** RN-09: pedidos creados desde el inicio del día */
    long countByFechaGreaterThanEqual(LocalDateTime inicioDia);

    /** RN-06: usos de un cupón (los pedidos cancelados no cuentan, RN-08) */
    long countByCupon_IdCuponAndEstadoNot(UUID idCupon, EstadoPedido estado);

    /** RN-10: pedidos de un día en un estado */
    List<Pedido> findByEstadoAndFechaGreaterThanEqualAndFechaLessThan(EstadoPedido estado,
                                                                      LocalDateTime inicio,
                                                                      LocalDateTime fin);
}
