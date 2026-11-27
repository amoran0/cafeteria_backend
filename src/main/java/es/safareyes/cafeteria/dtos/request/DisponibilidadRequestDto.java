package es.safareyes.cafeteria.dtos.request;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequestDto(@NotNull(message = "El campo disponible es obligatorio (true o false)") Boolean disponible) {
}
