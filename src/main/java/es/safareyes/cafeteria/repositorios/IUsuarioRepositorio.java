package es.safareyes.cafeteria.repositorios;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import es.safareyes.cafeteria.modelos.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepositorio extends JpaRepository<Usuario, UUID> {

    /** Login y JWTFilter: el mail es el nombre de usuario */
    Optional<Usuario> findByMailIgnoreCase(String mail);

    /** Registro: no puede haber dos usuarios con el mismo mail */
    boolean existsByMailIgnoreCase(String mail);
}
