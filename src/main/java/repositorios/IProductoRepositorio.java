package repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import modelos.Producto;

@Repository
public interface IProductoRepositorio extends JpaRepository<Producto, UUID> {


    @Query("""
        SELECT DISTINCT p FROM Producto p
        JOIN p.categorias c
        WHERE c.id = :categoriaId
          AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
          AND p.precioVenta <= :precioMaximo
          AND p.disponible = :disponible
          AND NOT EXISTS (
              SELECT 1 FROM p.alergenos a WHERE a.id = :alergenoId
          )
    """)

    @Query("""SELECT p.nombre, a.nombre FROM Producto p LEFT JOIN p.alergenos a WHERE p.id = :id""")
    List<Object[]> findNombreYNombreAlergenosPorId(@Param("id") UUID id);


    List<Producto> buscarPorCategoriaNombrePrecioDisponibilidadSinAlergeno(
        @Param("categoriaId") UUID categoriaId,
        @Param("nombre") String nombre,
        @Param("precioMaximo") BigDecimal precioMaximo,
        @Param("disponible") boolean disponible,
        @Param("alergenoId") UUID alergenoId
    );


}
