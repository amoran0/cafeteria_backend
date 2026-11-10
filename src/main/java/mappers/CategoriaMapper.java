package mappers;

import dtos.CategoriaResponseDto;
import modelos.Categoria;

public class CategoriaMapper {

    private CategoriaMapper() {
    }

    public static CategoriaResponseDto toDto(Categoria categoria) {
        return new CategoriaResponseDto(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDisponible()
        );
    }
}
