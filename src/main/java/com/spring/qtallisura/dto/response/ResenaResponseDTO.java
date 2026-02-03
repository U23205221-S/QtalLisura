package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResenaResponseDTO {

    private Integer idResena;
    private String clienteNombre;
    private String productoNombre;
    private String codigoPedido;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaResena;

}
