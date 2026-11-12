package es.safareyes.cafeteria.dtos;

import java.util.UUID;

public record CategoriasActivos(
        UUID idCategoria,
        String nombre,
        Boolean disponible,
        Long cantidadProductosActivos
) {
}
