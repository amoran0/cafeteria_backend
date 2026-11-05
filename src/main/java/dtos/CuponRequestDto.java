package dtos;

import jakarta.validation.constraints.*;
import modelos.TipoCupon;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuponRequestDto(
        @NotBlank String codigo,
        @NotNull TipoCupon tipo,
        @NotNull @Positive BigDecimal valor,
        BigDecimal importeMin,
        @PositiveOrZero Integer maxUsos,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
