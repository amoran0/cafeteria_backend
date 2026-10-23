package dtos;

import java.util.UUID;

public record CategoriasActivos(
        UUID idCategoria,
        String nombre,
        Long cantidadProductosActivos
) {
}