package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponseDTO {

    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String DNI;
    private String username;
    private String nombrePerfil;
    private LocalDateTime fechaRegistro;
    private String imagenUrl;
    private String estadoBD;

}
