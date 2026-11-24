package es.safareyes.cafeteria.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * clienteId es opcional: un CLIENTE siempre pide para sí mismo (sale de su token) y
 * un BARISTA puede indicarlo o dejarlo vacío para un pedido de barra sin cliente.
 */
public record PedidoRequestDto(
        UUID clienteId,
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
