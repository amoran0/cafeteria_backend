package dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaNombrePrecioDisponibilidad {
    private UUID idProducto;
    private String nombre;
    private BigDecimal precioVenta;
    private Boolean disponible;
}