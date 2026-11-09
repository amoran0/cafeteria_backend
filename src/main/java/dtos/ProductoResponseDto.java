package dtos;

import modelos.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductoResponseDto(
        UUID id,
        UUID categoriaId,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        BigDecimal precioVenta,
        Boolean disponible,
        Boolean activa,
        BigDecimal iva,
        List<AlergenoDto> alergenos
) {
    public record AlergenoDto(UUID id, String nombre) {
    }

    public static ProductoResponseDto of(Producto producto) {
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
                        : producto.getAlergenos().stream().map(a -> new AlergenoDto(a.getIdAlergeno(), a.getNombre())).toList()
        );
    }
}
