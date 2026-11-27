package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.dtos.response.ProducotsMasVendidos;
import es.safareyes.cafeteria.dtos.response.VentasPorDiaDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;

import java.time.LocalDate;
import java.util.List;

/** RF-07 / RN-10: solo cuentan los pedidos ENTREGADO (lo filtran las consultas nativas) */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class InformeService {
    private IPedidoRepositorio pedidoRepositorio;
    private IProductoRepositorio productoRepositorio;

    public VentasPorDiaDto ventasDia(LocalDate fecha) {
        return pedidoRepositorio.obtenerVentasDia(fecha);
    }

    public List<ProducotsMasVendidos> productosTop(LocalDate desde, LocalDate hasta, int limite) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("El parámetro 'limite' debe estar entre 1 y 100");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
        }
        return productoRepositorio.obtenerProductosTop(desde, hasta, limite);
    }
}
