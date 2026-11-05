package service;

import dtos.PedidoRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import modelos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repositorios.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final IPedidoRepositorio pedidoRepositorio;
    private final IClienteRepositorio clienteRepositorio;
    private final ICuponRepositorio cuponRepositorio;

    public Pedido crear(PedidoRequestDto dto) {
        Cliente cliente = clienteRepositorio.findById(dto.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setPrecioUnitario(dto.precioUnitario());
        pedido.setCupones(new ArrayList<>());
        if (dto.codigoCupon() != null && !dto.codigoCupon().isBlank()) {
            pedido.getCupones().add(cuponRepositorio.findByCodigoIgnoreCase(dto.codigoCupon())
                    .orElseThrow(() -> new EntityNotFoundException("Cupón no encontrado")));
        }

        return pedidoRepositorio.save(pedido);
    }

    public Pedido obtener(UUID id) {
        return pedidoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
    }

    public Pedido cambiarEstado(UUID id, EstadoPedido estado) {
        Pedido pedido = obtener(id);
        pedido.setEstado(estado);
        return pedidoRepositorio.save(pedido);
    }

    public Page<Pedido> listar(UUID clienteId, EstadoPedido estado, LocalDateTime desde,
                               LocalDateTime hasta, Pageable pageable) {
        if (clienteId != null) {
            return pedidoRepositorio.buscarPedidosCliente(clienteId, estado, desde, hasta, pageable);
        }
        return pedidoRepositorio.buscarPedidos(estado, desde, hasta, pageable);
    }
}
