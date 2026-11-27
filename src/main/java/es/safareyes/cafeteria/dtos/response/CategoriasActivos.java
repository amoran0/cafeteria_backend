package es.safareyes.cafeteria.dtos.response;

import java.util.UUID;

public record CategoriasActivos(
        UUID idCategoria,
        String nombre,
        Boolean disponible,
        Long cantidadProductosActivos
) {
}
