package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ReservaRequestDTO;
import com.spring.qtallisura.dto.response.ReservaResponseDTO;
import com.spring.qtallisura.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
@Slf4j
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@RequestBody ReservaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear una nueva reserva");
        ReservaResponseDTO dtoResponse = reservaService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<ReservaResponseDTO>> getAllReservas() {
        log.info("Recibida solicitud para obtener todas las reservas");
        Iterable<ReservaResponseDTO> reservas = reservaService.allList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener una reserva por su ID");
        return ResponseEntity.ok(reservaService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody ReservaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar una reserva por su ID");
        return ResponseEntity.ok(reservaService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar una reserva por su ID");
        reservaService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de reservas funciona correctamente");
    }

}
