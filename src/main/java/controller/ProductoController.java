package controller;

import dtos.DisponibilidadRequestDto;
import dtos.ProductoRequestDto;
import dtos.ProductoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ProductoService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/carta")
    public Page<ProductoResponseDto> carta(
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(required = false) Boolean disponible,
            Pageable pageable) {
        return productoService.listarCarta(categoriaId, nombre, precioMaximo, disponible, pageable)
                .map(ProductoResponseDto::of);
    }

    @GetMapping("/{id}")
    public ProductoResponseDto obtener(@PathVariable UUID id) {
        return ProductoResponseDto.of(productoService.obtenerProducto(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductoResponseDto.of(productoService.crearProducto(dto)));
    }

    @PutMapping("/{id}")
    public ProductoResponseDto modificar(@PathVariable UUID id, @Valid @RequestBody ProductoRequestDto dto) {
        return ProductoResponseDto.of(productoService.modificarProducto(id, dto));
    }

    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponseDto disponibilidad(@PathVariable UUID id,
                                              @Valid @RequestBody DisponibilidadRequestDto dto) {
        return ProductoResponseDto.of(productoService.cambiarDisponibilidad(id, dto.disponible()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable UUID id) {
        productoService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
