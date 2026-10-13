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
public class ProducotsMasVendidos {
    private UUID productoId;
    private String nombre;
    private Long unidadesVendidas;
    private BigDecimal totalVendido;
}