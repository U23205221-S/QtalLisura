package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.MovimientoInventarioRequestDTO;
import com.spring.qtallisura.dto.response.MovimientoInventarioResponseDTO;
import com.spring.qtallisura.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimiento-inventario")
@RequiredArgsConstructor
@Slf4j
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @PostMapping
    public ResponseEntity<MovimientoInventarioResponseDTO> createMovimientoInventario(@RequestBody MovimientoInventarioRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo movimiento de inventario");
        MovimientoInventarioResponseDTO dtoResponse = movimientoInventarioService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<MovimientoInventarioResponseDTO>> getAllMovimientosInventario() {
        log.info("Recibida solicitud para obtener todos los movimientos de inventario");
        Iterable<MovimientoInventarioResponseDTO> movimientos = movimientoInventarioService.allList();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un movimiento de inventario por su ID");
        return ResponseEntity.ok(movimientoInventarioService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody MovimientoInventarioRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un movimiento de inventario por su ID");
        return ResponseEntity.ok(movimientoInventarioService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un movimiento de inventario por su ID");
        movimientoInventarioService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de movimientos de inventario funciona correctamente");
    }

}
