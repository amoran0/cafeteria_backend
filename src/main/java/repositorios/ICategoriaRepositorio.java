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

    @Query("""
        SELECT new dtos.CategoriasActivos(
            c.idCategoria,
            c.nombre,
            COUNT(p)
        )
        FROM Categoria c
        LEFT JOIN Producto p ON p.categoria = c AND p.activa = true
        GROUP BY c.idCategoria, c.nombre
    """)
    List<CategoriasActivos> buscarCategoriasProductosActivos();
}