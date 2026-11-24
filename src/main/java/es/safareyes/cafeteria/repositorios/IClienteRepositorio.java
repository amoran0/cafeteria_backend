package es.safareyes.cafeteria.repositorios;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import es.safareyes.cafeteria.modelos.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepositorio extends JpaRepository<Cliente, UUID> {

    /** Ficha de cliente del usuario que ha iniciado sesión (pedidos propios) */
    Optional<Cliente> findByUsuario_IdUsuario(UUID idUsuario);
}
