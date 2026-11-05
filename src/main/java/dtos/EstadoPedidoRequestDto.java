package dtos;

import jakarta.validation.constraints.NotNull;
import modelos.EstadoPedido;

public record EstadoPedidoRequestDto(@NotNull EstadoPedido estado) {
}
