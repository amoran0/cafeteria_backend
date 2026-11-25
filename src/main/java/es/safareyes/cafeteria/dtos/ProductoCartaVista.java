package es.safareyes.cafeteria.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Q5 · Proyección por interfaz para la carta (endpoint 1).
 * Spring Data solo lee de la BD los campos que tienen getter aquí; los nombres de los getters
 * deben coincidir con los atributos de la entidad Producto.
 */
public interface ProductoCartaVista {

    UUID getIdProducto();

    String getNombre();

    String getDescripcion();

    BigDecimal getPrecioVenta();

    Boolean getDisponible();

    CategoriaView getCategoria();

    /** Proyección anidada: de la categoría solo se devuelven su id y su nombre */
    interface CategoriaView {
        UUID getIdCategoria();

        String getNombre();
    }
}
