package es.safareyes.cafeteria.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import es.safareyes.cafeteria.dtos.CategoriaNombrePrecioDisponibilidad;
import es.safareyes.cafeteria.dtos.ProducotsMasVendidos;
import es.safareyes.cafeteria.dtos.ProductoPorAlergeno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Producto;
import org.springframework.data.domain.Pageable;

@Repository
public interface IProductoRepositorio extends JpaRepository<Producto, UUID> {

    @Override
    @EntityGraph(attributePaths = {"categoria", "alergenos"})
    List<Producto> findAll();

    @Override
    @EntityGraph(attributePaths = {"categoria", "alergenos"})
    java.util.Optional<Producto> findById(UUID id);

    // ✅ Renombrado a mayúscula/minuscula exacta que pide el test
    @Query("""
        SELECT new es.safareyes.cafeteria.dtos.ProductoPorAlergeno(p.nombre, a.nombre)
        FROM Producto p
        LEFT JOIN p.alergenos a
    """)
    List<ProductoPorAlergeno> findNombreYNombreAlergenosPorId();

    // ✅ Nombre de método exacto que busca la línea 67 del test
    @Query("""
        SELECT DISTINCT new es.safareyes.cafeteria.dtos.CategoriaNombrePrecioDisponibilidad(
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
        SELECT DISTINCT new es.safareyes.cafeteria.dtos.CategoriaNombrePrecioDisponibilidad(
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

    List<Producto> findByCategoria_IdCategoria(UUID categoriaId);

    /** RN-10: ranking de productos de pedidos en el estado indicado (ENTREGADO) */
    @Query("""
        SELECT new es.safareyes.cafeteria.dtos.ProducotsMasVendidos(
            l.producto.idProducto, l.producto.nombre, SUM(l.cantidad), SUM(l.precioUnitario * l.cantidad))
        FROM LineaPedido l
        WHERE l.pedido.estado = :estado AND l.pedido.fecha >= :inicio AND l.pedido.fecha < :fin
        GROUP BY l.producto.idProducto, l.producto.nombre
        ORDER BY SUM(l.cantidad) DESC, l.producto.nombre
    """)
    List<ProducotsMasVendidos> obtenerProductosTop(
            @Param("estado") EstadoPedido estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            Pageable limite
    );
}
