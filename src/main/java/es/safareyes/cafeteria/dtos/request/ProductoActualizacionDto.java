package es.safareyes.cafeteria.dtos.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Modificación de un producto (PUT /api/v1/productos/{id}). Todos los campos son opcionales:
 * los que llegan a null no se cambian (NullValuePropertyMappingStrategy.IGNORE en ProductoMapper).
 * Si llega alergenosIds, sustituye la lista completa de alérgenos.
 */
public record ProductoActualizacionDto(
        UUID categoriaId,
        @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar en blanco")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String nombre,
        String descripcion,
        @PositiveOrZero(message = "El precio base no puede ser negativo")
        @Digits(integer = 6, fraction = 2, message = "El precio base admite como máximo 6 cifras enteras y 2 decimales")
        BigDecimal precioBase,
        @Positive(message = "El precio debe ser positivo")
        @Digits(integer = 6, fraction = 2, message = "El precio de venta admite como máximo 6 cifras enteras y 2 decimales")
        BigDecimal precioVenta,
        Boolean disponible,
        Boolean activa,
        BigDecimal iva,
        List<UUID> alergenosIds
) {
}
