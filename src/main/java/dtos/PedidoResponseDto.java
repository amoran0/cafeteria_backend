package dtos;

import modelos.EstadoPedido;

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
}
