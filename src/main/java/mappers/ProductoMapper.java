package mappers;

import dtos.ProductoRequestDto;
import dtos.ProductoResponseDto;
import modelos.Producto;

import java.util.List;

public class ProductoMapper {

    private ProductoMapper() {
    }

    public static ProductoResponseDto toDto(Producto producto) {
        return new ProductoResponseDto(
                producto.getIdProducto(),
                producto.getCategoria().getIdCategoria(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecioBase(),
                producto.getPrecioVenta(),
                producto.getDisponible(),
                producto.getActiva(),
                producto.getIva(),
                producto.getAlergenos() == null ? List.of()
                        : producto.getAlergenos().stream()
                        .map(a -> new ProductoResponseDto.AlergenoDto(a.getIdAlergeno(), a.getNombre()))
                        .toList()
        );
    }

    /** Copia los campos simples del DTO; la categoría y los alérgenos los pone el servicio */
    public static void actualizarEntidad(Producto producto, ProductoRequestDto dto) {
        producto.setNombre(dto.nombre().trim());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecioBase(dto.precioBase());
        producto.setPrecioVenta(dto.precioVenta());
        producto.setDisponible(dto.disponible() == null || dto.disponible());
        producto.setActiva(dto.activa() == null || dto.activa());
        producto.setIva(dto.iva());
    }
}
