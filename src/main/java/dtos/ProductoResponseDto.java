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
        List<UUID> alergenosIds
) {
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
                        : producto.getAlergenos().stream().map(a -> a.getIdAlergeno()).toList()
        );
    }
}
