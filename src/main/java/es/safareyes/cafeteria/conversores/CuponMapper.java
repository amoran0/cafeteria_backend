package es.safareyes.cafeteria.conversores;

import es.safareyes.cafeteria.dtos.CuponRequestDto;
import es.safareyes.cafeteria.dtos.CuponResponseDto;
import es.safareyes.cafeteria.modelos.Cupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuponMapper {

    @Mapping(target = "idCupon", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Cupon toEntity(CuponRequestDto dto);

    @Mapping(source = "idCupon", target = "id")
    CuponResponseDto toDto(Cupon cupon);
}
