package es.safareyes.cafeteria.dtos;

import java.util.UUID;

public record CategoriaResponseDto(
        UUID id,
        String nombre,
        boolean disponible
) {
}
