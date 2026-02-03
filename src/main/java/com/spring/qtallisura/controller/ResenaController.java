package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ResenaRequestDTO;
import com.spring.qtallisura.dto.response.ResenaResponseDTO;
import com.spring.qtallisura.service.ResenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resena")
@RequiredArgsConstructor
@Slf4j
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> createResena(@RequestBody ResenaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear una nueva reseña");
        ResenaResponseDTO dtoResponse = resenaService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<ResenaResponseDTO>> getAllResenas() {
        log.info("Recibida solicitud para obtener todas las reseñas");
        Iterable<ResenaResponseDTO> resenas = resenaService.allList();
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener una reseña por su ID");
        return ResponseEntity.ok(resenaService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody ResenaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar una reseña por su ID");
        return ResponseEntity.ok(resenaService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar una reseña por su ID");
        resenaService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de reseñas funciona correctamente");
    }

}
