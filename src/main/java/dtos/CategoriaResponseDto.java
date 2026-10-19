package dtos;

import modelos.Categoria;

import java.util.UUID;

public record CategoriaResponseDto(
        UUID id,
        String nombre,
        boolean disponible
) {

    public static CategoriaResponseDto of(Categoria categoria){
        return new CategoriaResponseDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.isDisponible()
        );
    }
}
