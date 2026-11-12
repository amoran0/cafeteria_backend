package es.safareyes.cafeteria.repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import es.safareyes.cafeteria.modelos.Alergeno;
import org.springframework.stereotype.Repository;

@Repository
public interface IAlergenoRepositorio extends JpaRepository<Alergeno, UUID> {
}
