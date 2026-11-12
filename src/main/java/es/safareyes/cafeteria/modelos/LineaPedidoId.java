package es.safareyes.cafeteria.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineaPedidoId implements Serializable {

    @Column(name = "id_pedido")
    private UUID idPedido;

    @Column(name = "id_producto")
    private UUID idProducto;
}
