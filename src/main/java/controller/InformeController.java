package controller;

import dtos.ProducotsMasVendidos;
import dtos.VentasPorDia;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.InformeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/informes")
public class InformeController {
    private final InformeService informeService;

    @GetMapping("/ventas-dia")
    public VentasPorDia ventasDia(@RequestParam LocalDate fecha) {
        return informeService.ventasDia(fecha);
    }

    @GetMapping("/productos-top")
    public List<ProducotsMasVendidos> productosTop(@RequestParam LocalDate desde,
                                                   @RequestParam LocalDate hasta,
                                                   @RequestParam(defaultValue = "10") int limite) {
        return informeService.productosTop(desde, hasta, limite);
    }
}
