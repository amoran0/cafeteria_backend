package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductoRequestDto(
        @NotNull(message = "La categoría es obligatoria") UUID categoriaId,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String nombre,
        String descripcion,
        @PositiveOrZero(message = "El precio base no puede ser negativo")
        @Digits(integer = 6, fraction = 2, message = "El precio base admite como máximo 6 cifras enteras y 2 decimales")
        BigDecimal precioBase,
        @NotNull(message = "El precio de venta es obligatorio")
        @Positive(message = "El precio debe ser positivo")
        @Digits(integer = 6, fraction = 2, message = "El precio de venta admite como máximo 6 cifras enteras y 2 decimales")
        BigDecimal precioVenta,
        Boolean disponible,
        @NotNull(message = "Activo o inactivo es obligatorio") Boolean activa,
        BigDecimal iva,
        List<UUID> alergenosIds
) {
}
