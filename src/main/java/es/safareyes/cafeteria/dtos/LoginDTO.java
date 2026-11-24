package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "El email es obligatorio") String email,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {
}
