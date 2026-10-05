package repositorios;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import modelos.Categoria;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, UUID> {

    @Query("""  SELECT c, COUNT(p) FROM Categoria c LEFT JOIN c.productos p ON p.activo = true GROUP BY c """)
    List<Object[]> findCategoriasConCantidadProductosActivos();

}
