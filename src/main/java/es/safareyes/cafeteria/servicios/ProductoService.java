package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.dtos.response.ProductoCartaVista;
import es.safareyes.cafeteria.dtos.request.ProductoActualizacionDto;
import es.safareyes.cafeteria.dtos.request.ProductoRequestDto;
import es.safareyes.cafeteria.dtos.response.ProductoResponseDto;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.conversores.ProductoMapper;
import es.safareyes.cafeteria.modelos.Alergeno;
import es.safareyes.cafeteria.modelos.Categoria;
import es.safareyes.cafeteria.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.safareyes.cafeteria.repositorios.IAlergenoRepositorio;
import es.safareyes.cafeteria.repositorios.ICategoriaRepositorio;
import es.safareyes.cafeteria.repositorios.IProductoRepositorio;
import es.safareyes.cafeteria.repositorios.ProductoSpecifications;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class ProductoService {
    private static final BigDecimal IVA_POR_DEFECTO = BigDecimal.TEN; // RN-05: hostelería, 10 %

    private IProductoRepositorio productoRepositorio;
    private ICategoriaRepositorio categoriaRepositorio;
    private IAlergenoRepositorio alergenoRepositorio;
    private ProductoMapper mapper;

    public ProductoResponseDto crearProducto(ProductoRequestDto dto) {
        Producto producto = new Producto();
        producto.setCategoria(buscarCategoria(dto.categoriaId()));
        mapper.actualizar(producto, dto);
        producto.setNombre(dto.nombre().trim());
        if (producto.getIva() == null) {
            producto.setIva(IVA_POR_DEFECTO);
        }
        producto.setAlergenos(buscarAlergenos(dto.alergenosIds() == null ? List.of() : dto.alergenosIds()));
        agotarSiCategoriaAgotada(producto);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    /** Un producto dado de baja no forma parte de la carta, así que tampoco tiene detalle (RN-01) */
    public ProductoResponseDto obtenerProducto(UUID id) {
        return mapper.toDto(buscarActivo(id));
    }

    /** Solo cambia los campos que llegan. Usa buscar() para que el ADMIN pueda reactivar un producto con "activa": true */
    public ProductoResponseDto modificarProducto(UUID id, ProductoActualizacionDto dto) {
        Producto producto = buscar(id);
        if (dto.categoriaId() != null) {
            producto.setCategoria(buscarCategoria(dto.categoriaId()));
        }
        mapper.actualizar(producto, dto);
        if (dto.nombre() != null) {
            producto.setNombre(dto.nombre().trim());
        }
        if (dto.alergenosIds() != null) {
            producto.setAlergenos(buscarAlergenos(dto.alergenosIds()));
        }
        agotarSiCategoriaAgotada(producto);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    public ProductoResponseDto cambiarDisponibilidad(UUID id, Boolean disponible) {
        Producto producto = buscarActivo(id);
        producto.setDisponible(disponible);
        return mapper.toDto(productoRepositorio.save(producto));
    }

    /** RN-01: baja lógica */
    public void darDeBaja(UUID id) {
        Producto producto = buscarActivo(id);
        producto.setActiva(false);
        productoRepositorio.save(producto);
    }

    /**
     * RF-02: carta con filtros combinables (Specification, Q7), paginada y ordenada en la BD,
     * devuelta con la proyección ProductoCartaView (Q5).
     * El orden por defecto (nombre) lo pone @PageableDefault en el controlador.
     */
    @Transactional(readOnly = true)
    public Page<ProductoCartaVista> listarCarta(UUID categoriaId, String nombre, BigDecimal precioMaximo,
                                                Boolean disponible, UUID sinAlergenoId, Pageable pageable) {
        return productoRepositorio.findBy(
                ProductoSpecifications.carta(categoriaId, nombre, precioMaximo, disponible, sinAlergenoId),
                consulta -> consulta.as(ProductoCartaVista.class).page(pageable));
    }

    private Producto buscar(UUID id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún producto con id " + id));
    }

    /** RN-01: un producto dado de baja no existe para la carta, el detalle, la disponibilidad ni el borrado */
    private Producto buscarActivo(UUID id) {
        return productoRepositorio.findByIdProductoAndActivaTrue(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún producto activo con id " + id + " (RN-01)"));
    }

    private Categoria buscarCategoria(UUID id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ninguna categoría con id " + id));
    }

    private List<Alergeno> buscarAlergenos(List<UUID> ids) {
        List<Alergeno> alergenos = alergenoRepositorio.findAllById(ids);
        if (alergenos.size() != new HashSet<>(ids).size()) {   // HashSet: un id repetido cuenta una sola vez
            throw new ElementoNoEncontrado("Uno o varios alérgenos indicados no existen");
        }
        return alergenos;
    }

    /** RF-03 / RN-02: un producto de una categoría agotada también está agotado */
    private void agotarSiCategoriaAgotada(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getCategoria().getDisponible())) {
            producto.setDisponible(false);
        }
    }
}
