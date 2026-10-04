package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Cliente;

public interface IClienteRepositorio extends JpaRepository<Cliente, UUID> {
}
