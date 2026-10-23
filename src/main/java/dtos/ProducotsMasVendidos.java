package dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProducotsMasVendidos(
        UUID productoId,
        String nombre,
        Long unidadesVendidas,
        BigDecimal totalVendido
) {
}