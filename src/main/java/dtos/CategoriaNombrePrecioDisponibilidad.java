package dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoriaNombrePrecioDisponibilidad(
        UUID idProducto,
        String nombre,
        BigDecimal precioVenta,
        Boolean disponible
) {
}