package dtos;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequestDto(@NotNull Boolean disponible) {
}
