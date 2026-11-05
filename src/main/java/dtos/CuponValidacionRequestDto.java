package dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CuponValidacionRequestDto(
        @NotBlank String codigo,
        @NotNull @jakarta.validation.constraints.PositiveOrZero BigDecimal subtotal
) {
}
