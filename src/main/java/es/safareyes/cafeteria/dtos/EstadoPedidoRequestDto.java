package es.safareyes.cafeteria.dtos;

import jakarta.validation.constraints.NotNull;
import es.safareyes.cafeteria.modelos.EstadoPedido;

public record EstadoPedidoRequestDto(@NotNull(message = "El estado es obligatorio") EstadoPedido estado) {
}
