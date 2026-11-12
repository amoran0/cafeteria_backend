package es.safareyes.cafeteria.conversores;

import es.safareyes.cafeteria.dtos.ProductoRequestDto;
import es.safareyes.cafeteria.dtos.ProductoResponseDto;
import es.safareyes.cafeteria.modelos.Alergeno;
import es.safareyes.cafeteria.modelos.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductoMapper {

    @Mapping(source = "idProducto", target = "id")
    @Mapping(source = "categoria.idCategoria", target = "categoriaId")
    ProductoResponseDto toDto(Producto producto);

    List<ProductoResponseDto> toDto(List<Producto> productos);

    @Mapping(source = "idAlergeno", target = "id")
    ProductoResponseDto.AlergenoDto toDto(Alergeno alergeno);

    // la categoría y los alérgenos los busca el servicio en sus repositorios
    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "alergenos", ignore = true)
    void actualizar(@MappingTarget Producto producto, ProductoRequestDto dto);
}
