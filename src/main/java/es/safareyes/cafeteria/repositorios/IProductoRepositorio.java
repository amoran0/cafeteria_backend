package es.safareyes.cafeteria.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import es.safareyes.cafeteria.dtos.CategoriaNombrePrecioDisponibilidad;
import es.safareyes.cafeteria.dtos.ProducotsMasVendidos;
import es.safareyes.cafeteria.dtos.ProductoPorAlergeno;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.Producto;
import org.springframework.data.domain.Pageable;

@Repository
public interface IProductoRepositorio extends JpaRepository<Producto, UUID>, JpaSpecificationExecutor<Producto> {

    @Override
    @EntityGraph(attributePaths = {"categoria", "alergenos"})
    List<Producto> findAll();

    /**
     * Carta (RF-02, Q7): filtros combinables con ProductoSpecifications, paginada y ordenada.
     * Solo se trae la categoría con JOIN: hacer fetch de la colección de alérgenos junto con la
     * paginación obligaría a Hibernate a paginar en memoria.
     */
    @Override
    @EntityGraph(attributePaths = {"categoria"})
    Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);

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

    /**
     * RF-03 (Q4): agota o repone de golpe todos los productos de una categoría.
     * flushAutomatically guarda antes los cambios pendientes; clearAutomatically vacía el contexto
     * para que no queden entidades Producto en memoria con el valor antiguo.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Producto p SET p.disponible = :disponible WHERE p.categoria.idCategoria = :categoriaId")
    int cambiarDisponibilidadCategoria(@Param("categoriaId") UUID categoriaId,
                                       @Param("disponible") Boolean disponible);

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
