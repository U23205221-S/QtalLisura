package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.PerfilModuloRequestDTO;
import com.spring.qtallisura.dto.response.PerfilModuloResponseDTO;
import com.spring.qtallisura.service.PerfilModuloService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil-modulo")
@RequiredArgsConstructor
@Slf4j
public class PerfilModuloController {

    private final PerfilModuloService perfilModuloService;

    @PostMapping
    public ResponseEntity<PerfilModuloResponseDTO> createPerfilModulo(@RequestBody PerfilModuloRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear perfil-modulo");
        PerfilModuloResponseDTO dtoResponse = perfilModuloService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<PerfilModuloResponseDTO>> getAllPerfilModulos() {
        log.info("Recibida solicitud para obtener todos los perfiles-modulos");
        Iterable<PerfilModuloResponseDTO> perfilModulos = perfilModuloService.allList();
        return ResponseEntity.ok(perfilModulos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilModuloResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un perfil-modulo por su ID");
        return ResponseEntity.ok(perfilModuloService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilModuloResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody PerfilModuloRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un perfil-modulo por su ID");
        return ResponseEntity.ok(perfilModuloService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un perfil-modulo por su ID");
        perfilModuloService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de perfil-modulo funciona correctamente");
    }

}
