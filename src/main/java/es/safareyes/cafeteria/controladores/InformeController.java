package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.ProducotsMasVendidos;
import es.safareyes.cafeteria.dtos.VentasPorDia;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import es.safareyes.cafeteria.servicios.InformeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/informes")
public class InformeController {
    private InformeService informeService;

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
