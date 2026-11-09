package dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PedidoRequestDto(
        UUID clienteId,                 // obligatorio si lo crea un barista; un cliente usa el suyo
        @NotEmpty(message = "El pedido debe tener líneas") List<@Valid Linea> lineas,
        String codigoCupon,
        String dniSocio
) {
    public record Linea(
            @NotNull(message = "El producto es obligatorio") UUID productoId,
            @NotNull(message = "La cantidad es obligatoria") Integer cantidad
    ) {
    }
}
