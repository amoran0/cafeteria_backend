package service;

import dtos.ProductoRequestDto;
import exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import mappers.ProductoMapper;
import modelos.Alergeno;
import modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repositorios.IAlergenoRepositorio;
import repositorios.ICategoriaRepositorio;
import repositorios.IProductoRepositorio;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final IProductoRepositorio productoRepositorio;
    private final ICategoriaRepositorio categoriaRepositorio;
    private final IAlergenoRepositorio alergenoRepositorio;

    public Producto crearProducto(ProductoRequestDto dto) {
        Producto producto = new Producto();
        actualizar(producto, dto);
        return productoRepositorio.save(producto);
    }

    public Producto obtenerProducto(UUID id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    public Producto modificarProducto(UUID id, ProductoRequestDto dto) {
        Producto producto = obtenerProducto(id);
        actualizar(producto, dto);
        return productoRepositorio.save(producto);
    }

    public Producto cambiarDisponibilidad(UUID id, Boolean disponible) {
        Producto producto = obtenerProducto(id);
        producto.setDisponible(disponible);
        return productoRepositorio.save(producto);
    }

    /** RN-01: baja lógica */
    public void darDeBaja(UUID id) {
        Producto producto = obtenerProducto(id);
        producto.setActiva(false);
        productoRepositorio.save(producto);
    }

    /** RF-02: carta con filtros (los dados de baja no aparecen) */
    public Page<Producto> listarCarta(UUID categoriaId, String nombre, BigDecimal precioMaximo,
                                      Boolean disponible, UUID sinAlergenoId, Pageable pageable) {
        String nombreFiltro = nombre == null ? null : nombre.trim().toLowerCase();
        List<Producto> filtrados = productoRepositorio.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getActiva()))
                .filter(p -> categoriaId == null || p.getCategoria().getIdCategoria().equals(categoriaId))
                .filter(p -> nombreFiltro == null || p.getNombre().toLowerCase().contains(nombreFiltro))
                .filter(p -> precioMaximo == null || p.getPrecioVenta().compareTo(precioMaximo) <= 0)
                .filter(p -> disponible == null || disponible.equals(p.getDisponible()))
                .filter(p -> sinAlergenoId == null || p.getAlergenos().stream()
                        .noneMatch(a -> a.getIdAlergeno().equals(sinAlergenoId)))
                .sorted(Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
        int inicio = (int) Math.min(pageable.getOffset(), filtrados.size());
        int fin = Math.min(inicio + pageable.getPageSize(), filtrados.size());
        return new PageImpl<>(filtrados.subList(inicio, fin), pageable, filtrados.size());
    }

    private void actualizar(Producto producto, ProductoRequestDto dto) {
        producto.setCategoria(categoriaRepositorio.findById(dto.categoriaId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada")));
        ProductoMapper.actualizarEntidad(producto, dto);
        List<UUID> ids = dto.alergenosIds() == null ? List.of() : dto.alergenosIds();
        List<Alergeno> alergenos = alergenoRepositorio.findAllById(ids);
        if (alergenos.size() != new HashSet<>(ids).size()) {
            throw new NotFoundException("Algún alérgeno indicado no existe");
        }
        producto.setAlergenos(alergenos);
    }
}
