package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuloResponseDTO {

    private String nombre;
    private String descripcion;

}
