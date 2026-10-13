package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class VentasPorDia {
    private Long numeroPedidos;
    private Double total;
    private Double base;
    private Double iva;

}
