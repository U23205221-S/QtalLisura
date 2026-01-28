package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.PerfilRequestDTO;
import com.spring.qtallisura.dto.response.PerfilResponseDTO;
import com.spring.qtallisura.service.PerfilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
@Slf4j
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    public ResponseEntity<PerfilResponseDTO> createPerfil(@RequestBody PerfilRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo perfil por defecto");
        PerfilResponseDTO dtoResponse = perfilService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<PerfilResponseDTO>> getAllPerfiles() {
        log.info("Recibida solicitud para obtener todos los perfiles");
        Iterable<PerfilResponseDTO> perfiles = perfilService.allList();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un perfil por su ID");
        return ResponseEntity.ok(perfilService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody PerfilRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un perfil por su ID");
        return ResponseEntity.ok(perfilService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un perfil por su ID");
        perfilService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de perfiles funciona correctamente");
    }

}
