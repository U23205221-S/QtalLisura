package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.MesaRequestDTO;
import com.spring.qtallisura.dto.response.MesaResponseDTO;
import com.spring.qtallisura.model.Mesa;
import com.spring.qtallisura.service.MesaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mesa")
@RequiredArgsConstructor
@Slf4j
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    public ResponseEntity<MesaResponseDTO> createMesa(@RequestBody MesaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear una nueva mesa");
        MesaResponseDTO dtoResponse = mesaService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<MesaResponseDTO>> getAllMesas() {
        log.info("Recibida solicitud para obtener todas las mesas");
        Iterable<MesaResponseDTO> mesas = mesaService.allList();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Iterable<MesaResponseDTO>> getMesasDisponibles() {
        log.info("Recibida solicitud para obtener mesas disponibles");
        Iterable<MesaResponseDTO> mesas = mesaService.findByEstado(Mesa.EstadoMesa.DISPONIBLE);
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener una mesa por su ID");
        return ResponseEntity.ok(mesaService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody MesaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar una mesa por su ID");
        return ResponseEntity.ok(mesaService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar una mesa por su ID");
        mesaService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de mesas funciona correctamente");
    }

}
