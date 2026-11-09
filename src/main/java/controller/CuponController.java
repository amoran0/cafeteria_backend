package controller;

import dtos.CuponRequestDto;
import dtos.CuponValidacionRequestDto;
import dtos.CuponValidacionResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modelos.Cupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import service.CuponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cupones")
public class CuponController {
    private final CuponService cuponService;

    @PostMapping
    public ResponseEntity<Cupon> crear(@Valid @RequestBody CuponRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.crear(dto));
    }

    @PostMapping("/validar")
    public CuponValidacionResponseDto validar(@Valid @RequestBody CuponValidacionRequestDto dto,
                                              Authentication auth) {
        return cuponService.validar(dto.codigo(), dto.subtotal(), auth.getName());
    }
}
