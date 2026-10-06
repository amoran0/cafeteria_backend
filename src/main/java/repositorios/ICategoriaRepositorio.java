package repositorios;

import java.util.List;
import java.util.UUID;

import dtos.CategoriasActivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import modelos.Categoria;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, UUID> {

    @Query(nativeQuery = true, value = """  SELECT c, COUNT(p) FROM Categoria c LEFT JOIN c.productos p 
                                    ON p.activo = true GROUP BY c """)
    List<CategoriasActivos> buscarCategoriasProductosActivos();

}
