package es.safareyes.cafeteria.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fila del ranking de productos más vendidos.
 */
public interface ProducotsMasVendidos {

    UUID getProductoId();

    String getNombre();

    Long getUnidadesVendidas();

    BigDecimal getTotalVendido();
}
