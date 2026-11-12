package es.safareyes.cafeteria.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PedidoRequestDto(
        @NotNull(message = "El cliente es obligatorio") UUID clienteId,
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
