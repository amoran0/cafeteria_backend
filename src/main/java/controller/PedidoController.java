package controller;

import dtos.EstadoPedidoRequestDto;
import dtos.PedidoRequestDto;
import dtos.PedidoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modelos.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PedidoService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDto> crear(@Valid @RequestBody PedidoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    @GetMapping("/{id}")
    public PedidoResponseDto obtener(@PathVariable UUID id) {
        return pedidoService.obtener(id);
    }

    @GetMapping
    public Page<PedidoResponseDto> listar(@RequestParam(required = false) UUID clienteId,
                                          @RequestParam(required = false) EstadoPedido estado,
                                          @RequestParam(required = false) LocalDate desde,
                                          @RequestParam(required = false) LocalDate hasta,
                                          Pageable pageable) {
        return pedidoService.listar(clienteId, estado,
                desde == null ? null : desde.atStartOfDay(),
                hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
                pageable);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponseDto cambiarEstado(@PathVariable UUID id,
                                           @Valid @RequestBody EstadoPedidoRequestDto dto) {
        return pedidoService.cambiarEstado(id, dto.estado());
    }
}
