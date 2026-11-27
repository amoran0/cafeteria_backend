package es.safareyes.cafeteria.conversores;

import es.safareyes.cafeteria.dtos.request.UsuarioDTO;
import es.safareyes.cafeteria.modelos.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "email", target = "mail")
    @Mapping(target = "contrasena", ignore = true)   // la contraseña se cifra a mano en el servicio
    Usuario toEntity(UsuarioDTO dto);
}
