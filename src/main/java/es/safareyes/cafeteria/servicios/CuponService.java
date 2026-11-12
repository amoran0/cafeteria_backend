package es.safareyes.cafeteria.servicios;

import es.safareyes.cafeteria.dtos.CuponRequestDto;
import es.safareyes.cafeteria.dtos.CuponValidacionResponseDto;
import es.safareyes.cafeteria.excepciones.ElementoDuplicado;
import es.safareyes.cafeteria.excepciones.ElementoNoEncontrado;
import es.safareyes.cafeteria.excepciones.ReglaNegocioException;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.conversores.CuponMapper;
import es.safareyes.cafeteria.modelos.Cupon;
import es.safareyes.cafeteria.modelos.EstadoPedido;
import es.safareyes.cafeteria.modelos.TipoCupon;
import org.springframework.stereotype.Service;
import es.safareyes.cafeteria.repositorios.ICuponRepositorio;
import es.safareyes.cafeteria.repositorios.IPedidoRepositorio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class CuponService {
    private ICuponRepositorio cuponRepositorio;
    private IPedidoRepositorio pedidoRepositorio;
    private CuponMapper mapper;

    public Cupon crear(CuponRequestDto dto) {
        String codigo = dto.codigo().trim().toUpperCase();
        if (cuponRepositorio.existsByCodigoIgnoreCase(codigo)) {
            throw new ElementoDuplicado("Ya existe un cupón con el código '" + codigo + "'; los códigos no pueden repetirse");
        }
        if (dto.fechaInicio() != null && dto.fechaFin() != null && dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin (" + dto.fechaFin() + ") no puede ser anterior a la de inicio (" + dto.fechaInicio() + ")");
        }
        if (dto.tipo() == TipoCupon.PORCENTAJE && dto.valor().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ReglaNegocioException("Un cupón de tipo PORCENTAJE no puede superar el 100 % y se ha indicado " + dto.valor() + " %");
        }
        Cupon cupon = mapper.toEntity(dto);
        cupon.setCodigo(codigo);
        return cuponRepositorio.save(cupon);
    }

    public CuponValidacionResponseDto validar(String codigo, BigDecimal subtotal, String emailCliente) {
        Cupon cupon = buscarValido(codigo, subtotal, emailCliente);
        BigDecimal descuento = calcularDescuento(cupon, subtotal);
        return new CuponValidacionResponseDto(cupon.getCodigo(), descuento, subtotal.subtract(descuento));
    }

    /** RN-06: existe, activo, en vigencia, con usos disponibles, subtotal mínimo y email si es personal */
    public Cupon buscarValido(String codigo, BigDecimal subtotal, String emailCliente) {
        Cupon cupon = cuponRepositorio.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new ElementoNoEncontrado("No existe ningún cupón con el código '" + codigo + "' (RN-06)"));
        LocalDate hoy = LocalDate.now();
        if (!Boolean.TRUE.equals(cupon.getActivo())) {
            throw new ReglaNegocioException("El cupón '" + cupon.getCodigo() + "' está desactivado (RN-06)");
        }
        if ((cupon.getFechaInicio() != null && hoy.isBefore(cupon.getFechaInicio()))
                || (cupon.getFechaFin() != null && hoy.isAfter(cupon.getFechaFin()))) {
            throw new ReglaNegocioException("El cupón '" + cupon.getCodigo() + "' solo es válido del " + cupon.getFechaInicio()
                    + " al " + cupon.getFechaFin() + " y hoy es " + hoy + " (RN-06)");
        }
        if (cupon.getMaxUsos() != null && usos(cupon) >= cupon.getMaxUsos()) {
            throw new ReglaNegocioException("El cupón '" + cupon.getCodigo() + "' ya ha alcanzado su máximo de " + cupon.getMaxUsos() + " usos (RN-06)");
        }
        if (cupon.getImporteMin() != null && subtotal.compareTo(cupon.getImporteMin()) < 0) {
            throw new ReglaNegocioException("El cupón '" + cupon.getCodigo() + "' exige un subtotal mínimo de " + cupon.getImporteMin()
                    + " € y el pedido suma " + subtotal + " € (RN-06)");
        }
        if (cupon.getEmail() != null && !cupon.getEmail().equalsIgnoreCase(emailCliente)) {
            throw new ReglaNegocioException("El cupón '" + cupon.getCodigo() + "' es personal y no pertenece al email "
                    + (emailCliente == null ? "(no indicado)" : emailCliente) + " (RN-06)");
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
