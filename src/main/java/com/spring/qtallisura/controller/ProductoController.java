package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ProductoRequestDTO;
import com.spring.qtallisura.dto.response.ProductoResponseDTO;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.service.FileStorageService;
import com.spring.qtallisura.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final ProductoService productoService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> createProducto(
            @Valid @RequestParam("idCategoria") Integer idCategoria,
            @Valid @RequestParam("nombre") String nombre,
            @Valid @RequestParam("descripcion") String descripcion,
            @Valid @RequestParam("precioVenta") Double precioVenta,
            @Valid @RequestParam("costoUnitario") Double costoUnitario,
            @Valid @RequestParam("stockMinimo") Integer stockMinimo,
            @Valid @RequestParam("stockActual") Integer stockActual,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Valid @RequestParam(value = "estadoBD", defaultValue = "ACTIVO") EstadoBD estadoBD) {
        log.info("Recibida solicitud para crear un nuevo producto");

        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            if (!fileStorageService.isValidImage(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            if (!fileStorageService.isValidSize(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            imagenUrl = fileStorageService.saveProductImage(imagen);
        }

        ProductoRequestDTO dtoRequest = ProductoRequestDTO.builder()
                .idCategoria(idCategoria)
                .nombre(nombre)
                .descripcion(descripcion)
                .precioVenta(precioVenta)
                .costoUnitario(costoUnitario)
                .stockMinimo(stockMinimo)
                .stockActual(stockActual)
                .imagenUrl(imagenUrl != null ? imagenUrl : "")
                .estadoBD(estadoBD)
                .build();

        ProductoResponseDTO dtoResponse = productoService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<ProductoResponseDTO>> getAllProductos() {
        log.info("Recibida solicitud para obtener todos los productos");
        Iterable<ProductoResponseDTO> productos = productoService.allList();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un producto por su ID");
        return ResponseEntity.ok(productoService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precioVenta", required = false) Double precioVenta,
            @RequestParam(value = "costoUnitario", required = false) Double costoUnitario,
            @RequestParam(value = "stockMinimo", required = false) Integer stockMinimo,
            @RequestParam(value = "stockActual", required = false) Integer stockActual,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "estadoBD", required = false) EstadoBD estadoBD) {
        log.info("Recibida solicitud para actualizar un producto por su ID");

        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            if (!fileStorageService.isValidImage(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            if (!fileStorageService.isValidSize(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            // Eliminar imagen anterior si existe
            ProductoResponseDTO productoActual = productoService.readById(id);
            if (productoActual.getImagenUrl() != null) {
                fileStorageService.deleteProductImage(productoActual.getImagenUrl());
            }
            imagenUrl = fileStorageService.saveProductImage(imagen);
        }

        ProductoRequestDTO dtoRequest = ProductoRequestDTO.builder()
                .idCategoria(idCategoria)
                .nombre(nombre)
                .descripcion(descripcion)
                .precioVenta(precioVenta)
                .costoUnitario(costoUnitario)
                .stockMinimo(stockMinimo)
                .stockActual(stockActual)
                .imagenUrl(imagenUrl)
                .estadoBD(estadoBD)
                .build();

        return ResponseEntity.ok(productoService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un producto por su ID");
        productoService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de productos funciona correctamente");
    }

}
