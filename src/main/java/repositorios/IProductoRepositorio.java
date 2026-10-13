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
        SELECT p.nombre AS productoNombre, a.nombre AS alergenoNombre
        FROM producto p
        LEFT JOIN producto_alergeno pa ON p.id_producto = pa.id_producto
        LEFT JOIN alergeno a ON pa.id_alergeno = a.id_alergeno
        WHERE p.id_producto = :id
    """)
    List<ProductoPorAlergeno> findNombreYNombreAlergenosPorId(@Param("id") UUID id);

    @Query(nativeQuery = true, value = """
        SELECT DISTINCT 
            p.id_producto AS idProducto,
            p.nombre AS nombre,
            p.precio_venta AS precioVenta,
            p.disponible AS disponible
        FROM producto p
        WHERE p.id_categoria = :categoriaId
          AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
          AND p.precio_venta <= :precioMaximo
          AND p.disponible = :disponible
          AND NOT EXISTS (
              SELECT 1
              FROM producto_alergeno pa
              WHERE pa.id_producto = p.id_producto
                AND pa.id_alergeno = :alergenoId
          )
    """)
    List<CategoriaNombrePrecioDisponibilidad> buscarPorCategoriaNombrePrecioDisponibilidadSinAlergeno(
            @Param("categoriaId") UUID categoriaId,
            @Param("nombre") String nombre,
            @Param("precioMaximo") Double precioMaximo,
            @Param("disponible") Boolean disponible,
            @Param("alergenoId") UUID alergenoId
    );

    @Query(nativeQuery = true, value = """
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
              OR p.fecha < (:hasta + CAST('1 day' AS INTERVAL))
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