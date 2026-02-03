package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.PagoRequestDTO;
import com.spring.qtallisura.dto.response.PagoResponseDTO;
import com.spring.qtallisura.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pago")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> createPago(@RequestBody PagoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo pago");
        PagoResponseDTO dtoResponse = pagoService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<PagoResponseDTO>> getAllPagos() {
        log.info("Recibida solicitud para obtener todos los pagos");
        Iterable<PagoResponseDTO> pagos = pagoService.allList();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un pago por su ID");
        return ResponseEntity.ok(pagoService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody PagoRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un pago por su ID");
        return ResponseEntity.ok(pagoService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un pago por su ID");
        pagoService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de pagos funciona correctamente");
    }

}
