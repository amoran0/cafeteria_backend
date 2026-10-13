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

    // ✅ Renombrado a mayúscula/minuscula exacta que pide el test
    @Query("""
        SELECT new dtos.ProductoPorAlergeno(p.nombre, a.nombre)
        FROM Producto p
        LEFT JOIN p.alergenos a
    """)
    List<ProductoPorAlergeno> findNombreYNombreAlergenosPorId();

    // ✅ Nombre de método exacto que busca la línea 67 del test
    @Query("""
        SELECT DISTINCT new dtos.CategoriaNombrePrecioDisponibilidad(
            p.idProducto,
            p.nombre,
            p.precioVenta,
            p.disponible
        )
        FROM Producto p
    """)
    List<CategoriaNombrePrecioDisponibilidad> CategoriaNombrePrecioDisponibilidad();

    // Método parametrizado para busquedas con filtro
    @Query("""
        SELECT DISTINCT new dtos.CategoriaNombrePrecioDisponibilidad(
            p.idProducto,
            p.nombre,
            p.precioVenta,
            p.disponible
        )
        FROM Producto p
        WHERE p.categoria.idCategoria = :categoriaId
          AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
          AND p.precioVenta <= :precioMaximo
          AND p.disponible = :disponible
          AND NOT EXISTS (
              SELECT 1 
              FROM p.alergenos a 
              WHERE a.idAlergeno = :alergenoId
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
            COALESCE(SUM(pp.cantidad * pp.precio_unitario), 0) AS totalVendido
        FROM pedido_producto pp
        INNER JOIN pedido p ON p.id_pedido = pp.id_pedido
        INNER JOIN producto pr ON pr.id_producto = pp.id_producto
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