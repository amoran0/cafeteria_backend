package repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import dtos.CategoriaNombrePrecioDisponibilidad;
import dtos.ProducotsMasVendidos;
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

    @Query(, nativeQuery = true, value = """
        SELECT
            pr.id_producto AS productoId,
            pr.nombre AS nombre,
            COALESCE(SUM(pp.cantidad), 0) AS unidadesVendidas,
            COALESCE(
                SUM(pp.cantidad * pp.precio_unitario), 0
            ) AS totalVendido
        FROM pedido_producto pp
        INNER JOIN pedido p
            ON p.id_pedido = pp.id_pedido
        INNER JOIN producto pr
            ON pr.id_producto = pp.id_producto
        WHERE (:desde IS NULL OR p.fecha >= :desde)
          AND (
              :hasta IS NULL
              OR p.fecha < DATE_ADD(:hasta, INTERVAL 1 DAY)
          )
        GROUP BY pr.id_producto, pr.nombre
        ORDER BY unidadesVendidas DESC, pr.nombre ASC
        LIMIT :limite
        """)
    List<ProducotsMasVendidos> obtenerProductosTop(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("limite") int limite
    );

}