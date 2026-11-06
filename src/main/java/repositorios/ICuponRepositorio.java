package repositorios;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Cupon;
import org.springframework.stereotype.Repository;

@Repository
public interface ICuponRepositorio extends JpaRepository<Cupon, UUID> {
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);
}
