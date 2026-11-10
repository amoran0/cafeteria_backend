package mappers;

import dtos.CuponRequestDto;
import modelos.Cupon;

public class CuponMapper {

    private CuponMapper() {
    }

    public static Cupon toEntity(CuponRequestDto dto) {
        Cupon cupon = new Cupon();
        cupon.setCodigo(dto.codigo().trim().toUpperCase());
        cupon.setTipo(dto.tipo());
        cupon.setValor(dto.valor());
        cupon.setImporteMin(dto.importeMin());
        cupon.setMaxUsos(dto.maxUsos());
        cupon.setFechaInicio(dto.fechaInicio());
        cupon.setFechaFin(dto.fechaFin());
        cupon.setEmail(dto.email());
        cupon.setActivo(true);
        return cupon;
    }
}
