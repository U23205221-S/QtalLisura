package com.spring.qtallisura.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaRequestDTO {

    @NotNull(message = "El id del cliente es obligatorio")
    private Integer idCliente;
    @NotNull(message = "El id del producto es obligatorio")
    private Integer idProducto;
    @NotNull(message = "El id del pedido es obligatorio")
    private Integer idPedido;
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación no puede ser mayor a 5")
    private Integer calificacion;
    @Size(max = 500, message = "El comentario no puede tener más de 500 caracteres")
    private String comentario;
    @NotNull(message = "La fecha de reseña es obligatoria")
    private LocalDateTime fechaResena;

}
