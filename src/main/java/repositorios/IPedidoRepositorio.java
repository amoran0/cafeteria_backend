package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Pedido;

public interface IPedidoRepositorio extends JpaRepository<Pedido, UUID> {
}
