package repositorios;

import java.util.List;
import java.util.UUID;

import dtos.CategoriaNombrePrecioDisponibilidad;
import dtos.ProductoPorAlergeno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import modelos.Producto;

@Repository
public interface IProductoRepositorio extends JpaRepository<Producto, UUID> {


    @Query(nativeQuery = true, value = """
        SELECT p.nombre, a.nombre
        FROM producto p
        LEFT JOIN producto_alergeno pa ON p.id = pa.producto_id
        LEFT JOIN alergeno a ON pa.alergeno_id = a.id
        WHERE p.id = :id
    """)
    List<ProductoPorAlergeno> findNombreYNombreAlergenosPorId();

    @Query(nativeQuery = true, value = """
        SELECT DISTINCT p.*
        FROM producto p
        JOIN producto_categoria pc ON p.id = pc.producto_id
        WHERE pc.categoria_id = :categoriaId
          AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
          AND p.precio_venta <= :precioMaximo
          AND p.disponible = :disponible
          AND NOT EXISTS (
              SELECT 1
              FROM producto_alergeno pa
              WHERE pa.producto_id = p.id
                AND pa.alergeno_id = :alergenoId
          )
    """ )
    List<CategoriaNombrePrecioDisponibilidad> BuscarPorCategoriaNombrePrecioDisponibilidadSinAlergeno();

}