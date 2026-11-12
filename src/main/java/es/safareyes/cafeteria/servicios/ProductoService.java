package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.dtos.ProductoRequestDto;
import es.safareyes.cafeteria.dtos.ProductoResponseDto;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.conversores.ProductoMapper;
import es.safareyes.cafeteria.modelos.Alergeno;
import es.safareyes.cafeteria.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.cafeteria.repositorios.IAlergenoRepositorio;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class ProductoService {
    private IProductoRepositorio productoRepositorio;
    private ICategoriaRepositorio categoriaRepositorio;
    private IAlergenoRepositorio alergenoRepositorio;
    private ProductoMapper mapper;

    public ProductoResponseDto crearProducto(ProductoRequestDto dto) {
        Producto producto = new Producto();
        actualizar(producto, dto);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    public ProductoResponseDto obtenerProducto(UUID id) {
        return mapper.toDto(buscar(id));
    }

    public ProductoResponseDto modificarProducto(UUID id, ProductoRequestDto dto) {
        Producto producto = buscar(id);
        actualizar(producto, dto);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    public ProductoResponseDto cambiarDisponibilidad(UUID id, Boolean disponible) {
        Producto producto = buscar(id);
        producto.setDisponible(disponible);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    /** RN-01: baja lógica */
    public void darDeBaja(UUID id) {
        Producto producto = buscar(id);
        producto.setActiva(false);
        productoRepositorio.save(producto);
    }

    /** RF-02: carta con filtros (los dados de baja no aparecen) */
    public Page<ProductoResponseDto> listarCarta(UUID categoriaId, String nombre, BigDecimal precioMaximo,
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
        return new PageImpl<>(mapper.toDto(filtrados.subList(inicio, fin)), pageable, filtrados.size());
    }

    private Producto buscar(UUID id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún producto con id " + id));
    }

    private void actualizar(Producto producto, ProductoRequestDto dto) {
        producto.setCategoria(categoriaRepositorio.findById(dto.categoriaId())
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ninguna categoría con id " + dto.categoriaId())));
        mapper.actualizar(producto, dto);
        producto.setNombre(dto.nombre().trim());
        List<UUID> ids = dto.alergenosIds() == null ? List.of() : dto.alergenosIds();
        List<Alergeno> alergenos = alergenoRepositorio.findAllById(ids);
        if (alergenos.size() != new HashSet<>(ids).size()) {
            List<UUID> encontrados = alergenos.stream().map(Alergeno::getIdAlergeno).toList();
            List<UUID> noExisten = ids.stream().filter(i -> !encontrados.contains(i)).distinct().toList();
            throw new ElementoNoEncontrado("No existen los alérgenos con id " + noExisten);
        }
        producto.setAlergenos(alergenos);
    }
}
