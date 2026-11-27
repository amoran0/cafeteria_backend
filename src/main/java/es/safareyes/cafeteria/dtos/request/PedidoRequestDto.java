package es.safareyes.cafeteria.dtos.request;

import es.safareyes.cafeteria.validaciones.DniValido;
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
        @DniValido String dniSocio   // opcional: solo para el descuento de socio FitZone
) {
    public record Linea(
            @NotNull(message = "El producto es obligatorio") UUID productoId,
            @NotNull(message = "La cantidad es obligatoria") Integer cantidad
    ) {
    }
}
