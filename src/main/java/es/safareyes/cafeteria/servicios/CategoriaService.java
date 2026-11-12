package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.conversores.CategoriaMapper;
import es.safareyes.cafeteria.dtos.CategoriaResponseDto;
import es.safareyes.cafeteria.dtos.CategoriasActivos;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.modelos.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoriaService {

    private ICategoriaRepositorio categoriaRepositorio;
    private IProductoRepositorio productoRepositorio;
    private CategoriaMapper mapper;

    public List<CategoriasActivos> listarCategorias() {
        return categoriaRepositorio.buscarCategoriasProductosActivos();
    }

    /** RF-03: agota o repone la categoría y todos sus productos */
    @Transactional
    public CategoriaResponseDto cambiarDisponibilidad(UUID id, Boolean disponible) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ninguna categoría con id " + id));
        categoria.setDisponible(disponible);
        productoRepositorio.findByCategoria_IdCategoria(id)
                .forEach(p -> p.setDisponible(disponible));
        return mapper.toDto(categoria);
    }
}
