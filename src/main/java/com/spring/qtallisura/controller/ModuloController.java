package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ModuloRequestDTO;
import com.spring.qtallisura.dto.response.ModuloResponseDTO;
import com.spring.qtallisura.service.ModuloService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modulo")
@RequiredArgsConstructor
@Slf4j
public class ModuloController {

    private final ModuloService moduloService;

    @PostMapping
    public ResponseEntity<ModuloResponseDTO> createModulo(@RequestBody ModuloRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo módulo");
        ModuloResponseDTO dtoResponse = moduloService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<ModuloResponseDTO>> getAllModulos() {
        log.info("Recibida solicitud para obtener todos los módulos");
        Iterable<ModuloResponseDTO> modulos = moduloService.allList();
        return ResponseEntity.ok(modulos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un módulo por su ID");
        return ResponseEntity.ok(moduloService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody ModuloRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un módulo por su ID");
        return ResponseEntity.ok(moduloService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un módulo por su ID");
        moduloService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de módulos funciona correctamente");
    }

}
