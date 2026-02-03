package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MovimientoInventarioResponseDTO {

    private Integer idMovimiento;
    private String productoNombre;
    private String tipoMovimiento;
    private Integer cantidad;
    private LocalDateTime fechaMovimiento;

}
