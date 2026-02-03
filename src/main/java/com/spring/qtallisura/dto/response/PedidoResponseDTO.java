package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponseDTO {

    private Integer idPedido;
    private String clienteNombre;
    private String usuarioNombre;
    private Integer numeroMesa;
    private String codigo;
    private String estadoPedido;
    private Double total;
    private LocalDateTime fechaPedido;
    private String estadoBD;

}
