package dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductoRequestDto(

        @NotNull(message = "La categoría es obligatoria")
        UUID categoriaId,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        BigDecimal precioBase,

        @NotNull(message = "El precio de venta es obligatorio")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal precioVenta,

        Boolean disponible,

        @NotNull(message = "Activo o inactivo es obligatorio")
        Boolean activa,

        BigDecimal iva,

        List<UUID> alergenosIds

) {}