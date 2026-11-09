package service;

import dtos.CategoriasActivos;
import exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import modelos.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositorios.ICategoriaRepositorio;
import repositorios.IProductoRepositorio;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final ICategoriaRepositorio categoriaRepositorio;
    private final IProductoRepositorio productoRepositorio;

    public List<CategoriasActivos> listarCategorias() {
        return categoriaRepositorio.buscarCategoriasProductosActivos();
    }

    /** RF-03: agota o repone la categoría y todos sus productos */
    @Transactional
    public Categoria cambiarDisponibilidad(UUID id, Boolean disponible) {
        Categoria categoria = categoriaRepositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        categoria.setDisponible(disponible);
        productoRepositorio.findByCategoria_IdCategoria(id)
                .forEach(p -> p.setDisponible(disponible));
        return categoria;
    }
}
