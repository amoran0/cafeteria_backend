package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Alergeno;

public interface IAlergenoRepositorio extends JpaRepository<Alergeno, UUID> {
}
