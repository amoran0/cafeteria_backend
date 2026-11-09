package dtos;

import modelos.EstadoPedido;
import modelos.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponseDto(
        UUID id,
        UUID clienteId,
        Integer numeroTurno,
        EstadoPedido estado,
        LocalDateTime fecha,
        List<Linea> lineas,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        BigDecimal baseImponible,
        BigDecimal cuotaIva,
        String codigoCupon
) {
    public record Linea(UUID productoId, String nombre, Integer cantidad, BigDecimal precioUnitario) {
    }

    public static PedidoResponseDto of(Pedido p) {
        return new PedidoResponseDto(
                p.getIdPedido(),
                p.getCliente().getIdCliente(),
                p.getNumeroTurno(),
                p.getEstado(),
                p.getFecha(),
                p.getLineas().stream()
                        .map(l -> new Linea(l.getProducto().getIdProducto(), l.getProducto().getNombre(),
                                l.getCantidad(), l.getPrecioUnitario()))
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
