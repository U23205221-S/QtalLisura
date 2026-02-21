package com.spring.qtallisura.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequestDTO {

    @NotNull(message = "El id del pedido es obligatorio")
    private Integer idPedido;
    @NotNull(message = "El id del producto es obligatorio")
    private Integer idProducto;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 99, message = "La cantidad no puede ser mayor a 99")
    private Integer cantidad;
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser un valor positivo")
    private Double precioUnitario;
    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser un valor positivo")
    private Double subtotal;

}
