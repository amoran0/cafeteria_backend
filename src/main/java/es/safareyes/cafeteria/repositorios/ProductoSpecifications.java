package es.safareyes.cafeteria.repositorios;

import es.safareyes.cafeteria.modelos.Producto;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Carta con filtros combinables (RF-02, Q7). Cada filtro es una condición del WHERE;
 * si su parámetro llega a null devuelve null y Spring Data lo ignora.
 */
public class ProductoSpecifications {

    public static Specification<Producto> carta(UUID categoriaId, String nombre, BigDecimal precioMaximo,
                                                Boolean disponible, UUID sinAlergenoId) {
        return activa()
                .and(deCategoria(categoriaId))
                .and(nombreContiene(nombre))
                .and(precioHasta(precioMaximo))
                .and(disponible(disponible))
                .and(sinAlergeno(sinAlergenoId));
    }

    /** RN-01: los productos dados de baja nunca salen en la carta */
    private static Specification<Producto> activa() {
        return (root, query, cb) -> cb.isTrue(root.get("activa"));
    }

    private static Specification<Producto> deCategoria(UUID categoriaId) {
        return (root, query, cb) -> categoriaId == null ? null
                : cb.equal(root.get("categoria").get("idCategoria"), categoriaId);
    }

    private static Specification<Producto> nombreContiene(String nombre) {
        return (root, query, cb) -> nombre == null || nombre.isBlank() ? null
                : cb.like(cb.lower(root.get("nombre")), "%" + nombre.trim().toLowerCase() + "%");
    }

    private static Specification<Producto> precioHasta(BigDecimal precioMaximo) {
        return (root, query, cb) -> precioMaximo == null ? null
                : cb.lessThanOrEqualTo(root.get("precioVenta"), precioMaximo);
    }

    private static Specification<Producto> disponible(Boolean disponible) {
        return (root, query, cb) -> disponible == null ? null
                : cb.equal(root.get("disponible"), disponible);
    }

    /** NOT EXISTS (SELECT 1 FROM alérgenos de ESTE producto WHERE id = sinAlergenoId) */
    private static Specification<Producto> sinAlergeno(UUID alergenoId) {
        return (root, query, cb) -> {
            if (alergenoId == null) {
                return null;
            }
            Subquery<Integer> conAlergeno = query.subquery(Integer.class);
            var alergeno = conAlergeno.correlate(root).join("alergenos");
            conAlergeno.select(cb.literal(1)).where(cb.equal(alergeno.get("idAlergeno"), alergenoId));
            return cb.not(cb.exists(conAlergeno));
        };
    }
}
