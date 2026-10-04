package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Usuario;

public interface IUsuarioRepositorio extends JpaRepository<Usuario, UUID> {
}
