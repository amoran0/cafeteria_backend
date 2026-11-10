package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepositorio extends JpaRepository<Cliente, UUID> {
}
