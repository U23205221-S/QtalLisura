package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfilResponseDTO {

    private String nombrePerfil;
    private String descripcionPerfil;

}
