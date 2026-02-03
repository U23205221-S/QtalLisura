package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetallePedidoResponseDTO {

    private Integer idDetallePedido;
    private Integer idPedido;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

}
