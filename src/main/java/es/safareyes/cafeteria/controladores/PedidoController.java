package es.safareyes.cafeteria.controladores;

import es.safareyes.cafeteria.dtos.EstadoPedidoRequestDto;
import es.safareyes.cafeteria.dtos.PedidoRequestDto;
import es.safareyes.cafeteria.dtos.PedidoResponseDto;
import es.safareyes.cafeteria.modelos.Usuario;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import es.safareyes.cafeteria.modelos.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import es.safareyes.cafeteria.servicios.PedidoService;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private PedidoService pedidoService;

    /** @AuthenticationPrincipal: el usuario del token, que deja el JWTFilter en el SecurityContext */
    @PostMapping
    @PreAuthorize("hasAnyRole('BARISTA', 'CLIENTE')")
    public ResponseEntity<PedidoResponseDto> crear(@Valid @RequestBody PedidoRequestDto dto,
                                                   @AuthenticationPrincipal Usuario usuario) {
        PedidoResponseDto creado = pedidoService.crear(dto, usuario);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping("/{id}")
    public PedidoResponseDto obtener(@PathVariable UUID id, @AuthenticationPrincipal Usuario usuario) {
        return pedidoService.obtener(id, usuario);
    }

    @GetMapping
    public Page<PedidoResponseDto> listar(@RequestParam(required = false) UUID clienteId,
                                          @RequestParam(required = false) EstadoPedido estado,
                                          @RequestParam(required = false) LocalDate desde,
                                          @RequestParam(required = false) LocalDate hasta,
                                          Pageable pageable,
                                          @AuthenticationPrincipal Usuario usuario) {
        return pedidoService.listar(clienteId, estado,
                desde == null ? null : desde.atStartOfDay(),
                hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
                pageable, usuario);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'BARISTA')")
    public PedidoResponseDto cambiarEstado(@PathVariable UUID id,
                                           @Valid @RequestBody EstadoPedidoRequestDto dto) {
        return pedidoService.cambiarEstado(id, dto.estado());
    }
}
