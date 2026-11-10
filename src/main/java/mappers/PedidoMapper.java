package mappers;

import dtos.PedidoResponseDto;
import modelos.Pedido;

public class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoResponseDto toDto(Pedido p) {
        return new PedidoResponseDto(
                p.getIdPedido(),
                p.getCliente().getIdCliente(),
                p.getNumeroTurno(),
                p.getEstado(),
                p.getFecha(),
                p.getLineas().stream()
                        .map(l -> new PedidoResponseDto.Linea(l.getProducto().getIdProducto(),
                                l.getProducto().getNombre(), l.getCantidad(), l.getPrecioUnitario()))
                        .toList(),
                p.getSubtotal(),
                p.getDescuento(),
                p.getTotal(),
                p.getBaseImponible(),
                p.getCuotaIva(),
                p.getCupon() == null ? null : p.getCupon().getCodigo()
        );
    }
}
