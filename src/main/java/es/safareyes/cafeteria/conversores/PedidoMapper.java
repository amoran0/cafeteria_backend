package es.safareyes.cafeteria.conversores;

import es.safareyes.cafeteria.dtos.PedidoResponseDto;
import es.safareyes.cafeteria.modelos.LineaPedido;
import es.safareyes.cafeteria.modelos.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(source = "idPedido", target = "id")
    @Mapping(source = "cliente.idCliente", target = "clienteId")
    @Mapping(source = "cupon.codigo", target = "codigoCupon")
    PedidoResponseDto toDto(Pedido pedido);

    @Mapping(source = "producto.idProducto", target = "productoId")
    @Mapping(source = "producto.nombre", target = "nombre")
    PedidoResponseDto.Linea toDto(LineaPedido linea);
}
