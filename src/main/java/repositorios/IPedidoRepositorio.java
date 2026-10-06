package repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import modelos.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Pedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedidoRepositorio extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p")
    List<Pedido> obtenerTodosPedidos();

    List<Pedido> findByCliente_IdAndEstadoAndFechaBetween(
            UUID clienteId,
            EstadoPedido estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

}
