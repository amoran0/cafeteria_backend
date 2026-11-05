package controller;

import dtos.ProductoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.ProductoService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductoCartaController {
    private final ProductoService productoService;

    @GetMapping("/productosCarta")
    public Page<ProductoResponseDto> carta(
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(required = false) Boolean disponible,
            Pageable pageable) {
        return productoService.listarCarta(categoriaId, nombre, precioMaximo, disponible, pageable)
                .map(ProductoResponseDto::of);
    }
}
