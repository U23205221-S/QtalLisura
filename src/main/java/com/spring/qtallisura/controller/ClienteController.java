package com.spring.qtallisura.controller;

import com.spring.qtallisura.dto.request.ClienteRequestDTO;
import com.spring.qtallisura.dto.response.ClienteResponseDTO;
import com.spring.qtallisura.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(@RequestBody ClienteRequestDTO dtoRequest) {
        log.info("Recibida solicitud para crear un nuevo cliente");
        ClienteResponseDTO dtoResponse = clienteService.create(dtoRequest);
        return ResponseEntity.status(201).body(dtoResponse);
    }

    @GetMapping
    public ResponseEntity<Iterable<ClienteResponseDTO>> getAllClientes() {
        log.info("Recibida solicitud para obtener todos los clientes");
        Iterable<ClienteResponseDTO> clientes = clienteService.allList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> readById(@PathVariable Integer id) {
        log.info("Recibida solicitud para obtener un cliente por su ID");
        return ResponseEntity.ok(clienteService.readById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> updateById(
            @PathVariable Integer id,
            @RequestBody ClienteRequestDTO dtoRequest) {
        log.info("Recibida solicitud para actualizar un cliente por su ID");
        return ResponseEntity.ok(clienteService.updateById(id, dtoRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Integer id) {
        log.info("Recibida solicitud para eliminar un cliente por su ID");
        clienteService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El controlador de clientes funciona correctamente");
    }

}
