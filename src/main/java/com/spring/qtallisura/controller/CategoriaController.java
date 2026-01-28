package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.CategoriaRequestDTO;
import com.spring.qtallisura.dto.response.CategoriaResponseDTO;
import com.spring.qtallisura.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> createCategoria(@RequestBody CategoriaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear una nueva categoría");
        CategoriaResponseDTO dtoResponse = categoriaService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<CategoriaResponseDTO>> getAllCategorias() {
        log.info("Recibida solicitud para obtener todas las categorías");
        Iterable<CategoriaResponseDTO> categorias = categoriaService.allList();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener una categoría por su ID");
        return ResponseEntity.ok(categoriaService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody CategoriaRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar una categoría por su ID");
        return ResponseEntity.ok(categoriaService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar una categoría por su ID");
        categoriaService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de categorías funciona correctamente");
    }

}
