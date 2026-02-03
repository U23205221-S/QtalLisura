package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PagoResponseDTO {

    private Integer idPago;
    private Integer idPedido;
    private String metodoPago;
    private UUID codigoTransaccion;
    private Double monto;
    private LocalDateTime fechaPago;
    private String estadoPago;

}
