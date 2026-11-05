package service;

import dtos.CuponRequestDto;
import dtos.CuponValidacionResponseDto;
import lombok.RequiredArgsConstructor;
import modelos.Cupon;
import modelos.TipoCupon;
import org.springframework.stereotype.Service;
import repositorios.ICuponRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CuponService {
    private final ICuponRepositorio cuponRepositorio;

    public Cupon crear(CuponRequestDto dto) {
        Cupon cupon = new Cupon();
        cupon.setCodigo(dto.codigo().trim().toUpperCase());
        cupon.setTipo(dto.tipo());
        cupon.setValor(dto.valor());
        cupon.setImporteMin(dto.importeMin());
        cupon.setMaxUsos(dto.maxUsos());
        cupon.setFechaInicio(dto.fechaInicio());
        cupon.setFechaFin(dto.fechaFin());
        return cuponRepositorio.save(cupon);
    }

    public CuponValidacionResponseDto validar(String codigo, BigDecimal subtotal) {
        Cupon cupon = cuponRepositorio.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado"));
        LocalDate hoy = LocalDate.now();
        if ((cupon.getFechaInicio() != null && hoy.isBefore(cupon.getFechaInicio()))
                || (cupon.getFechaFin() != null && hoy.isAfter(cupon.getFechaFin()))
                || (cupon.getImporteMin() != null && subtotal.compareTo(cupon.getImporteMin()) < 0)) {
            throw new IllegalArgumentException("El cupón no es válido para este subtotal o fecha");
        }
        BigDecimal descuento = cupon.getTipo() == TipoCupon.PORCENTAJE
                ? subtotal.multiply(cupon.getValor()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : cupon.getValor();
        descuento = descuento.min(subtotal).max(BigDecimal.ZERO);
        return new CuponValidacionResponseDto(cupon.getCodigo(), descuento, subtotal.subtract(descuento));
    }
}
