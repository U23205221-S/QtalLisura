package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.UsuarioRequestDTO;
import com.spring.qtallisura.dto.response.UsuarioResponseDTO;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.service.FileStorageService;
import com.spring.qtallisura.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(
            @Valid @RequestParam("nombresUsuario") String nombresUsuario,
            @Valid @RequestParam("apellidosUsuario") String apellidosUsuario,
            @Valid @RequestParam("DNI") String DNI,
            @Valid @RequestParam("username") String username,
            @Valid @RequestParam("contrasena") String contrasena,
            @Valid @RequestParam("idPerfil") Integer idPerfil,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @Valid @RequestParam(value = "estadoBD", defaultValue = "ACTIVO") EstadoBD estadoBD) {
        log.info("Recibida solicitud para crear un nuevo usuario");

        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            if (!fileStorageService.isValidImage(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            if (!fileStorageService.isValidSize(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            imagenUrl = fileStorageService.saveUserImage(imagen);
        }

        UsuarioRequestDTO dtoRequest = UsuarioRequestDTO.builder()
                .nombresUsuario(nombresUsuario)
                .apellidosUsuario(apellidosUsuario)
                .DNI(DNI)
                .username(username)
                .contrasena(contrasena)
                .idPerfil(idPerfil)
                .imagenUrl(imagenUrl)
                .estadoBD(estadoBD)
                .build();

        UsuarioResponseDTO dtoResponse = usuarioService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<UsuarioResponseDTO>> getAllUsuarios() {
        log.info("Recibida solicitud para obtener todos los usuarios");
        Iterable<UsuarioResponseDTO> usuarios = usuarioService.allList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un usuario por su ID");
        return ResponseEntity.ok(usuarioService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestParam(value = "nombresUsuario", required = false) String nombresUsuario,
            @RequestParam(value = "apellidosUsuario", required = false) String apellidosUsuario,
            @RequestParam(value = "DNI", required = false) String DNI,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "idPerfil", required = false) Integer idPerfil,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "estadoBD", required = false) EstadoBD estadoBD) {
        log.info("Recibida solicitud para actualizar un usuario por su ID");

        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            if (!fileStorageService.isValidImage(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            if (!fileStorageService.isValidSize(imagen)) {
                return ResponseEntity.badRequest().build();
            }
            // Eliminar imagen anterior si existe
            UsuarioResponseDTO usuarioActual = usuarioService.readById(id);
            if (usuarioActual.getImagenUrl() != null) {
                fileStorageService.deleteUserImage(usuarioActual.getImagenUrl());
            }
            imagenUrl = fileStorageService.saveUserImage(imagen);
        }

        UsuarioRequestDTO dtoRequest = UsuarioRequestDTO.builder()
                .nombresUsuario(nombresUsuario)
                .apellidosUsuario(apellidosUsuario)
                .DNI(DNI)
                .username(username)
                .contrasena(contrasena)
                .idPerfil(idPerfil)
                .imagenUrl(imagenUrl)
                .estadoBD(estadoBD)
                .build();

        return ResponseEntity.ok(usuarioService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un usuario por su ID");
        usuarioService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de usuarios funciona correctamente");
    }

}
