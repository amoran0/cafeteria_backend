package dtos;

import java.math.BigDecimal;

public record VentasPorDia(
        Long numeroPedidos,
        BigDecimal total,
        BigDecimal base,
        BigDecimal iva
) {
}