package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ProducotsMasVendidos {
    private String nombreProducto;
    private Long cantidadVendida;
}
