package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ProductoPorAlergeno {
    private String productoNombre;
    private String alergenoNombre;
}
