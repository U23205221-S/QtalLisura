package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponseDTO {

    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String DNI;
    private String estadoBD;

}
