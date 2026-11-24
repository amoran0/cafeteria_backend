package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.CuponRequestDto;
import es.safareyes.cafeteria.dtos.CuponResponseDto;
import es.safareyes.cafeteria.dtos.CuponValidacionRequestDto;
import es.safareyes.cafeteria.dtos.CuponValidacionResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import es.safareyes.cafeteria.servicios.CuponService;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cupones")
public class CuponController {
    private CuponService cuponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuponResponseDto> crear(@Valid @RequestBody CuponRequestDto dto) {
        CuponResponseDto creado = cuponService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PostMapping("/validar")
    public CuponValidacionResponseDto validar(@Valid @RequestBody CuponValidacionRequestDto dto) {
        return cuponService.validar(dto.codigo(), dto.subtotal(), dto.email());
    }
}
