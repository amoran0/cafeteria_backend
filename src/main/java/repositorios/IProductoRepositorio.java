package repositorios;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import modelos.Producto;

public interface IProductoRepositorio extends JpaRepository<Producto, UUID> {
}
