package service;

import dtos.ProducotsMasVendidos;
import dtos.VentasPorDia;
import lombok.RequiredArgsConstructor;
import modelos.EstadoPedido;
import modelos.Pedido;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositorios.IPedidoRepositorio;
import repositorios.IProductoRepositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/** RF-07 / RN-10: solo cuentan los pedidos ENTREGADO */
@Service
@RequiredArgsConstructor
public class InformeService {
    private final IPedidoRepositorio pedidoRepositorio;
    private final IProductoRepositorio productoRepositorio;

    @Transactional(readOnly = true)
    public VentasPorDia ventasDia(LocalDate fecha) {
        List<Pedido> pedidos = pedidoRepositorio.findByEstadoAndFechaGreaterThanEqualAndFechaLessThan(
                EstadoPedido.ENTREGADO, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
        return new VentasPorDia(
                (long) pedidos.size(),
                sumar(pedidos, Pedido::getTotal),
                sumar(pedidos, Pedido::getBaseImponible),
                sumar(pedidos, Pedido::getCuotaIva));
    }

    public List<ProducotsMasVendidos> productosTop(LocalDate desde, LocalDate hasta, int limite) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("El límite debe estar entre 1 y 100");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a 'desde'");
        }
        return productoRepositorio.obtenerProductosTop(EstadoPedido.ENTREGADO,
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), PageRequest.of(0, limite));
    }

    private BigDecimal sumar(List<Pedido> pedidos, Function<Pedido, BigDecimal> importe) {
        return pedidos.stream().map(importe).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
