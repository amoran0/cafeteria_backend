package dtos;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class CategoriaNombrePrecioDisponibilidad {
    private String categoriaNombre;
    private String productoNombre;
    private BigDecimal precio;
    private Boolean disponible;
    private UUID alergenoId;
}
