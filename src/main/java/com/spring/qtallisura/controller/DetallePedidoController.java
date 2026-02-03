package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.DetallePedidoRequestDTO;
import com.spring.qtallisura.dto.response.DetallePedidoResponseDTO;
import com.spring.qtallisura.service.DetallePedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detalle-pedido")
@RequiredArgsConstructor
@Slf4j
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @PostMapping
    public ResponseEntity<DetallePedidoResponseDTO> createDetallePedido(@RequestBody DetallePedidoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo detalle de pedido");
        DetallePedidoResponseDTO dtoResponse = detallePedidoService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<DetallePedidoResponseDTO>> getAllDetallesPedido() {
        log.info("Recibida solicitud para obtener todos los detalles de pedido");
        Iterable<DetallePedidoResponseDTO> detalles = detallePedidoService.allList();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un detalle de pedido por su ID");
        return ResponseEntity.ok(detallePedidoService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody DetallePedidoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un detalle de pedido por su ID");
        return ResponseEntity.ok(detallePedidoService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un detalle de pedido por su ID");
        detallePedidoService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de detalles de pedido funciona correctamente");
    }

}
