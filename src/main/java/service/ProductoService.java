package service;

import excepciones.CategoriaNoEncontradaException;
import dtos.ProductoRequestDto;
import lombok.RequiredArgsConstructor;
import modelos.Categoria;
import modelos.Producto;
import org.springframework.stereotype.Service;
import repositorios.ICategoriaRepositorio;
import repositorios.IProductoRepositorio;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ICategoriaRepositorio categoriaRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final AlergenoService alergenoService;

    public Producto crearProducto(ProductoRequestDto dto) {
        Categoria categoria = categoriaRepositorio.findById(dto.categoriaId())
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se ha encontrado la categoría con ID: " + dto.categoriaId()
                ));

        Producto producto = new Producto();
        producto.setCategoria(categoria);
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecioBase(dto.precioBase());
        producto.setPrecioVenta(dto.precioVenta());
        producto.setDisponible(dto.disponible() == null || dto.disponible());
        producto.setActiva(dto.activa());
        producto.setIva(dto.iva());
        producto.setAlergenos(alergenoService.buscarPorIds(dto.alergenosIds()));
        return productoRepositorio.save(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepositorio.findAll();
    }
}
