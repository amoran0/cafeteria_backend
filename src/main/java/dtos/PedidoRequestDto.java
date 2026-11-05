package dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PedidoRequestDto(
        @NotNull UUID clienteId,
        String codigoCupon,
        String dniSocio,
        BigDecimal precioUnitario
) {
}
