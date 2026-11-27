package es.safareyes.cafeteria.dtos.response;

import java.math.BigDecimal;

public record CuponValidacionResponseDto(
        String codigo,
        BigDecimal descuento,
        BigDecimal total
) {
}
