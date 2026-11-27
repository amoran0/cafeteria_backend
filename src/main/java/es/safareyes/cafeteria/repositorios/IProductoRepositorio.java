package es.safareyes.cafeteria.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.safareyes.cafeteria.dtos.response.ProducotsMasVendidos;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Optional<Producto> findById(UUID id);

    /** RN-01: un producto dado de baja no tiene detalle ni se puede modificar su disponibilidad o borrar */
    @EntityGraph(attributePaths = {"categoria", "alergenos"})
    Optional<Producto> findByIdProductoAndActivaTrue(UUID idProducto);

    /**
     * RF-03 (Q4): agota o repone de golpe todos los productos de una categoría.
     * flushAutomatically guarda antes los cambios pendientes; clearAutomatically vacía el contexto
     * para que no queden entidades Producto en memoria con el valor antiguo.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Producto p SET p.disponible = :disponible WHERE p.categoria.idCategoria = :categoriaId")
    int cambiarDisponibilidadCategoria(@Param("categoriaId") UUID categoriaId,
                                       @Param("disponible") Boolean disponible);

    /**
     * RF-07 / RN-10 (Q3): ranking de productos más vendidos con SQL nativo y LIMIT.
     * Solo cuentan los pedidos ENTREGADO. DATE_TRUNC('day', fecha) quita la hora, así que BETWEEN
     * incluye completos el día "desde" y el día "hasta". Los alias van entre comillas para que
     * PostgreSQL respete las mayúsculas.
     */
    @Query(value = """
        SELECT pr.id_producto                       AS "productoId",
               pr.nombre                            AS "nombre",
               SUM(l.cantidad)                      AS "unidadesVendidas",
               SUM(l.cantidad * l.precio_unitario)  AS "totalVendido"
        FROM pedido_producto l
        JOIN pedido p    ON p.id_pedido = l.id_pedido
        JOIN producto pr ON pr.id_producto = l.id_producto
        WHERE p.estado = 'ENTREGADO'
          AND DATE_TRUNC('day', p.fecha) BETWEEN :desde AND :hasta
        GROUP BY pr.id_producto, pr.nombre
        ORDER BY SUM(l.cantidad) DESC, pr.nombre
        LIMIT :limite
    """, nativeQuery = true)
    List<ProducotsMasVendidos> obtenerProductosTop(@Param("desde") LocalDate desde,
                                                   @Param("hasta") LocalDate hasta,
                                                   @Param("limite") int limite);
}
