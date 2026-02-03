package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Mesa;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MesaRequestDTO {

    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número de mesa debe ser mayor a 0")
    @Max(value = 99, message = "El número de mesa no puede ser mayor a 99")
    private Integer numeroMesa;
    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    @Max(value = 99, message = "La capacidad máxima no puede ser mayor a 99")
    private Integer capacidadMaxima;
    @NotNull(message = "El estado de la mesa es obligatorio")
    private Mesa.EstadoMesa estadoMesa;
    @NotNull(message = "La ubicación es obligatoria")
    private Mesa.Ubicacion ubicacion;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
