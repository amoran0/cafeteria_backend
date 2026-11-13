package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.CuponRequestDto;
import es.safareyes.cafeteria.dtos.CuponResponseDto;
import es.safareyes.cafeteria.dtos.CuponValidacionRequestDto;
import es.safareyes.cafeteria.dtos.CuponValidacionResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.safareyes.cafeteria.servicios.CuponService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cupones")
public class CuponController {
    private CuponService cuponService;

    @PostMapping
    public ResponseEntity<CuponResponseDto> crear(@Valid @RequestBody CuponRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.crear(dto));
    }

    @PostMapping("/validar")
    public CuponValidacionResponseDto validar(@Valid @RequestBody CuponValidacionRequestDto dto) {
        return cuponService.validar(dto.codigo(), dto.subtotal(), dto.email());
    }
}
