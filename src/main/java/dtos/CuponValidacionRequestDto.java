package dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CuponValidacionRequestDto(
        @NotBlank String codigo,
        @NotNull @PositiveOrZero BigDecimal subtotal,
        @Email String email          // opcional: necesario para validar cupones personales (RN-06)
) {
}
