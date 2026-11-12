package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.CategoriaResponseDto;
import es.safareyes.cafeteria.dtos.CategoriasActivos;
import es.safareyes.cafeteria.dtos.DisponibilidadRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import es.safareyes.cafeteria.servicios.CategoriaService;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private CategoriaService categoriaService;

    @GetMapping
    public List<CategoriasActivos> listar() {
        return categoriaService.listarCategorias();
    }

    @PatchMapping("/{id}/disponibilidad")
    public CategoriaResponseDto disponibilidad(@PathVariable UUID id,
                                               @Valid @RequestBody DisponibilidadRequestDto dto) {
        return categoriaService.cambiarDisponibilidad(id, dto.disponible());
    }
}
