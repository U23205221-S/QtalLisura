package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponseDTO {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;
    private String estadoBD;
}
