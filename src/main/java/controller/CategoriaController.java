package controller;

import dtos.CategoriaResponseDto;
import dtos.CategoriasActivos;
import dtos.DisponibilidadRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.CategoriaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriasActivos> listar() {
        return categoriaService.listarCategorias();
    }

    @PatchMapping("/{id}/disponibilidad")
    public CategoriaResponseDto disponibilidad(@PathVariable UUID id,
                                               @Valid @RequestBody DisponibilidadRequestDto dto) {
        return CategoriaResponseDto.of(categoriaService.cambiarDisponibilidad(id, dto.disponible()));
    }
}
