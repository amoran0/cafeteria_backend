package es.safareyes.cafeteria.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Registro: el email es el nombre de usuario */
public record UsuarioDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido") String email,
        @NotBlank(message = "La contraseña es obligatoria") String password,
        @NotBlank(message = "Hay que repetir la contraseña") String confirmPassword
) {
}
