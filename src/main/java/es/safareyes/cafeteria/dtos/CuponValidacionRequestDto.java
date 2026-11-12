package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CuponValidacionRequestDto(
        @NotBlank(message = "El código es obligatorio") String codigo,
        @NotNull(message = "El subtotal es obligatorio")
        @PositiveOrZero(message = "El subtotal no puede ser negativo") BigDecimal subtotal,
        @Email(message = "El email no tiene un formato válido") String email          // opcional: necesario para validar cupones personales (RN-06)
) {
}
