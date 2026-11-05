package controller;

import dtos.ProducotsMasVendidos;
import dtos.VentasPorDia;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import repositorios.IPedidoRepositorio;
import repositorios.IProductoRepositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/informes")
public class InformeController {
    private final IPedidoRepositorio pedidoRepositorio;
    private final IProductoRepositorio productoRepositorio;

    @GetMapping("/ventas-dia")
    public VentasPorDia ventasDia(@RequestParam LocalDate fecha) {
        List<VentasPorDia> resultado = pedidoRepositorio
                .obtenerEstadisticasPedidosPorDia(fecha.atStartOfDay());
        return resultado.isEmpty() ? new VentasPorDia(0L, java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO) : resultado.getFirst();
    }

    @GetMapping("/productos-top")
    public List<ProducotsMasVendidos> productosTop(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limite) {
        if (limite < 1 || limite > 100) {
            throw new IllegalArgumentException("El límite debe estar entre 1 y 100");
        }
        return productoRepositorio.obtenerProductosTop(desde, hasta, limite);
    }
}
