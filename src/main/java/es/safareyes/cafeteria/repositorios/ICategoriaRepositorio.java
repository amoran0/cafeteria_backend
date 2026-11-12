package es.safareyes.cafeteria.repositorios;

import java.util.List;
import java.util.UUID;

import es.safareyes.cafeteria.dtos.CategoriasActivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.safareyes.cafeteria.modelos.Categoria;

@Repository
public interface ICategoriaRepositorio extends JpaRepository<Categoria, UUID> {

    @Query("""
        SELECT new es.safareyes.cafeteria.dtos.CategoriasActivos(
            c.idCategoria,
            c.nombre,
            c.disponible,
            COUNT(p)
        )
        FROM Categoria c
        LEFT JOIN Producto p ON p.categoria = c AND p.activa = true
        GROUP BY c.idCategoria, c.nombre, c.disponible
    """)
    List<CategoriasActivos> buscarCategoriasProductosActivos();
}