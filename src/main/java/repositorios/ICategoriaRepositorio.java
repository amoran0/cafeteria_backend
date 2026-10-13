package repositorios;

import java.util.List;
import java.util.UUID;

import dtos.CategoriasActivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import modelos.Categoria;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, UUID> {

    @Query(nativeQuery = true, value = """
        SELECT 
            c.id_categoria AS idCategoria,
            c.nombre AS nombre,
            COUNT(p.id_producto) AS cantidadProductosActivos
        FROM categoria c
        LEFT JOIN producto p 
            ON p.id_categoria = c.id_categoria AND p.activa = true
        GROUP BY c.id_categoria, c.nombre
        """)
    List<CategoriasActivos> buscarCategoriasProductosActivos();
}