package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.PedidoRequestDTO;
import com.spring.qtallisura.dto.response.PedidoResponseDTO;
import com.spring.qtallisura.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(@RequestBody PedidoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo pedido");
        PedidoResponseDTO dtoResponse = pedidoService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<PedidoResponseDTO>> getAllPedidos() {
        log.info("Recibida solicitud para obtener todos los pedidos");
        Iterable<PedidoResponseDTO> pedidos = pedidoService.allList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un pedido por su ID");
        return ResponseEntity.ok(pedidoService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody PedidoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un pedido por su ID");
        return ResponseEntity.ok(pedidoService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un pedido por su ID");
        pedidoService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de pedidos funciona correctamente");
    }

}
