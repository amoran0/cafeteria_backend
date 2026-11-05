package controller;

import dtos.EstadoPedidoRequestDto;
import dtos.PedidoRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modelos.EstadoPedido;
import modelos.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PedidoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable UUID id) {
        return pedidoService.obtener(id);
    }

    @GetMapping
    public Page<Pedido> listar(@RequestParam(required = false) UUID clienteId,
                               @RequestParam(required = false) EstadoPedido estado,
                               @RequestParam(required = false) LocalDate desde,
                               @RequestParam(required = false) LocalDate hasta,
                               Pageable pageable) {
        LocalDateTime inicio = desde == null ? LocalDateTime.MIN : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? LocalDateTime.MAX : hasta.plusDays(1).atStartOfDay();
        return pedidoService.listar(clienteId, estado, inicio, fin, pageable);
    }

    @PatchMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable UUID id,
                                @Valid @RequestBody EstadoPedidoRequestDto dto) {
        return pedidoService.cambiarEstado(id, dto.estado());
    }
}
