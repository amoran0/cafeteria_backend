package dtos;

import jakarta.validation.constraints.*;
import modelos.TipoCupon;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuponRequestDto(
        @NotBlank String codigo,
        @NotNull TipoCupon tipo,
        @NotNull @Positive BigDecimal valor,
        @PositiveOrZero BigDecimal importeMin,
        @Positive Integer maxUsos,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        @Email String email          // opcional: cupón personal
) {
}
