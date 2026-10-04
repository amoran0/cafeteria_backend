package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Cupon;

public interface ICuponRepositorio extends JpaRepository<Cupon, UUID> {
}
