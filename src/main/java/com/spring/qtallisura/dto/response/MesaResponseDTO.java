package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaResponseDTO {

    private Integer idMesa;
    private Integer numeroMesa;
    private Integer capacidadMaxima;
    private String estadoMesa;
    private String ubicacion;
    private String estadoBD;

}
