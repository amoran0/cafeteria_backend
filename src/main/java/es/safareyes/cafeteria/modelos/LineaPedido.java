package es.safareyes.cafeteria.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_producto")
@Getter
@Setter
@NoArgsConstructor
public class LineaPedido {

    @EmbeddedId
    private LineaPedidoId id = new LineaPedidoId();

    @MapsId("idPedido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @MapsId("idProducto")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /** RN-03: precio vigente al crear el pedido */
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
}
