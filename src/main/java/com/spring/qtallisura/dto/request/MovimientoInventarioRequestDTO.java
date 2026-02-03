package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.MovimientoInventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MovimientoInventarioRequestDTO {

    @NotNull(message = "El id del producto es obligatorio")
    private Integer idProducto;
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MovimientoInventario.TipoMovimiento tipoMovimiento;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    @NotNull(message = "La fecha de movimiento es obligatoria")
    private LocalDateTime fechaMovimiento;

}
