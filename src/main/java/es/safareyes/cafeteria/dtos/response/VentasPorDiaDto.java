package es.safareyes.cafeteria.dtos.response;

import java.math.BigDecimal;

/**
 * Informe de ventas del día (endpoint 15). Proyección por interfaz de la consulta nativa
 * IPedidoRepositorio.obtenerVentasDia: cada getter coincide con un alias de la consulta.
 */
public interface VentasPorDiaDto {

    Long getNumeroPedidos();

    BigDecimal getTotal();

    BigDecimal getBase();

    BigDecimal getIva();
}
