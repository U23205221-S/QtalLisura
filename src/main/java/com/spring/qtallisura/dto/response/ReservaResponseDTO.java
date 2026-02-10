package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservaResponseDTO {

    private Integer idReserva;
    private Integer idCliente;
    private String clienteNombre;
    private Integer idMesa;
    private Integer numeroMesa;
    private LocalDateTime fechaHora;
    private Integer telefono;
    private String estadoSolicitud;
    private String notasEspeciales;
    private String estadoBD;

}
