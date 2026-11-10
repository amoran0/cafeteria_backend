package service;

import dtos.CuponRequestDto;
import dtos.CuponValidacionResponseDto;
import exceptions.ConflictoException;
import exceptions.NotFoundException;
import exceptions.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import mappers.CuponMapper;
import modelos.Cupon;
import modelos.EstadoPedido;
import modelos.TipoCupon;
import org.springframework.stereotype.Service;
import repositorios.ICuponRepositorio;
import repositorios.IPedidoRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CuponService {
    private final ICuponRepositorio cuponRepositorio;
    private final IPedidoRepositorio pedidoRepositorio;

    public Cupon crear(CuponRequestDto dto) {
        String codigo = dto.codigo().trim().toUpperCase();
        if (cuponRepositorio.existsByCodigoIgnoreCase(codigo)) {
            throw new ConflictoException("Ya existe un cupón con el código " + codigo);
        }
        if (dto.fechaInicio() != null && dto.fechaFin() != null && dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (dto.tipo() == TipoCupon.PORCENTAJE && dto.valor().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ReglaNegocioException("Un cupón de porcentaje no puede superar el 100 %");
        }
        return cuponRepositorio.save(CuponMapper.toEntity(dto));
    }

    public CuponValidacionResponseDto validar(String codigo, BigDecimal subtotal, String emailCliente) {
        Cupon cupon = buscarValido(codigo, subtotal, emailCliente);
        BigDecimal descuento = calcularDescuento(cupon, subtotal);
        return new CuponValidacionResponseDto(cupon.getCodigo(), descuento, subtotal.subtract(descuento));
    }

    /** RN-06: existe, activo, en vigencia, con usos disponibles, subtotal mínimo y email si es personal */
    public Cupon buscarValido(String codigo, BigDecimal subtotal, String emailCliente) {
        Cupon cupon = cuponRepositorio.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new NotFoundException("El cupón " + codigo + " no existe"));
        LocalDate hoy = LocalDate.now();
        if (!Boolean.TRUE.equals(cupon.getActivo())) {
            throw new ReglaNegocioException("El cupón no está activo");
        }
        if ((cupon.getFechaInicio() != null && hoy.isBefore(cupon.getFechaInicio()))
                || (cupon.getFechaFin() != null && hoy.isAfter(cupon.getFechaFin()))) {
            throw new ReglaNegocioException("El cupón está fuera de su periodo de vigencia");
        }
        if (cupon.getMaxUsos() != null && usos(cupon) >= cupon.getMaxUsos()) {
            throw new ReglaNegocioException("El cupón ha alcanzado su número máximo de usos");
        }
        if (cupon.getImporteMin() != null && subtotal.compareTo(cupon.getImporteMin()) < 0) {
            throw new ReglaNegocioException("El subtotal no alcanza el importe mínimo del cupón");
        }
        if (cupon.getEmail() != null && !cupon.getEmail().equalsIgnoreCase(emailCliente)) {
            throw new ReglaNegocioException("El cupón es personal de otro cliente");
        }
        return cupon;
    }

    public BigDecimal calcularDescuento(Cupon cupon, BigDecimal subtotal) {
        BigDecimal descuento = cupon.getTipo() == TipoCupon.PORCENTAJE
                ? subtotal.multiply(cupon.getValor()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : cupon.getValor();
        return descuento.min(subtotal).max(BigDecimal.ZERO);
    }

    /** Usos = pedidos no cancelados con el cupón (al cancelar se devuelve el uso, RN-08) */
    private long usos(Cupon cupon) {
        return pedidoRepositorio.countByCupon_IdCuponAndEstadoNot(cupon.getIdCupon(), EstadoPedido.CANCELADO);
    }
}
