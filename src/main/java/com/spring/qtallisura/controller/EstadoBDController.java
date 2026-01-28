package com.spring.qtallisura.controller;

import com.spring.qtallisura.model.EstadoBD;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enum")
public class EstadoBDController {
    @GetMapping("/estados-bd")
    public ResponseEntity<List<Map<String, Object>>> obtenerEstadosBD() {
        List<Map<String, Object>> estados = Arrays.stream(EstadoBD.values())
                .<Map<String, Object>>map(estado -> Map.of(
                        "clave", estado.name(),
                        "nombre", estado.getNombre(),
                        "id", estado.getId()
                ))
                .toList();
        return ResponseEntity.ok(estados);
    }
}
