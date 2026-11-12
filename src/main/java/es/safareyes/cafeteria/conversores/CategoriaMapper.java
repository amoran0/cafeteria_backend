package es.safareyes.cafeteria.conversores;

import es.safareyes.cafeteria.dtos.CategoriaResponseDto;
import es.safareyes.cafeteria.modelos.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(source = "idCategoria", target = "id")
    CategoriaResponseDto toDto(Categoria categoria);
}
