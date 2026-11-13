package es.safareyes.cafeteria.dtos;

import es.safareyes.cafeteria.modelos.TipoCupon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CuponResponseDto(
        UUID id,
        String codigo,
        TipoCupon tipo,
        BigDecimal valor,
        BigDecimal importeMin,
        Integer maxUsos,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean activo,
        String email
) {
}
