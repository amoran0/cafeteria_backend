package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductoRequestDto(
        @NotNull(message = "La categoría es obligatoria") UUID categoriaId,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        String descripcion,
        BigDecimal precioBase,
        @NotNull(message = "El precio de venta es obligatorio")
        @Positive(message = "El precio debe ser positivo") BigDecimal precioVenta,
        Boolean disponible,
        Boolean activa,
        BigDecimal iva,
        List<UUID> alergenosIds
) {
}
