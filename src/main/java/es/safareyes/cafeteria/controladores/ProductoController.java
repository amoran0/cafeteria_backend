package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.DisponibilidadRequestDto;
import es.safareyes.cafeteria.dtos.ProductoCartaVista;
import es.safareyes.cafeteria.dtos.ProductoRequestDto;
import es.safareyes.cafeteria.dtos.ProductoResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import es.safareyes.cafeteria.servicios.ProductoService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private ProductoService productoService;

    @GetMapping
    public Page<ProductoCartaVista> carta(
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) UUID sinAlergenoId,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {   // sin ?sort= se ordena por nombre
        return productoService.listarCarta(categoriaId, nombre, precioMaximo, disponible, sinAlergenoId, pageable);
    }

    @GetMapping("/{id}")
    public ProductoResponseDto obtener(@PathVariable UUID id) {
        return productoService.obtenerProducto(id);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto creado = productoService.crearProducto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ProductoResponseDto modificar(@PathVariable UUID id, @Valid @RequestBody ProductoRequestDto dto) {
        return productoService.modificarProducto(id, dto);
    }

    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponseDto disponibilidad(@PathVariable UUID id,
                                              @Valid @RequestBody DisponibilidadRequestDto dto) {
        return productoService.cambiarDisponibilidad(id, dto.disponible());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable UUID id) {
        productoService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
