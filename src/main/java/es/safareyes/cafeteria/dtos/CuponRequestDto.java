package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.*;
import es.safareyes.cafeteria.modelos.TipoCupon;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuponRequestDto(
        @NotBlank(message = "El código es obligatorio") String codigo,
        @NotNull(message = "El tipo es obligatorio (PORCENTAJE o FIJO)") TipoCupon tipo,
        @NotNull(message = "El valor es obligatorio") @Positive(message = "El valor debe ser mayor que 0") BigDecimal valor,
        @PositiveOrZero(message = "El importe mínimo no puede ser negativo") BigDecimal importeMin,
        @Positive(message = "El máximo de usos debe ser mayor que 0") Integer maxUsos,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        @Email(message = "El email no tiene un formato válido") String email          // opcional: cupón personal
) {
}
