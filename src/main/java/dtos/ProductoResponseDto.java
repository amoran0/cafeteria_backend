package dtos;

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
}
