package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepositorio extends JpaRepository<Usuario, UUID> {
}
