package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Reserva;
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
public class ReservaRequestDTO {

    @NotNull(message = "El id del cliente es obligatorio")
    private Integer idCliente;
    @NotNull(message = "El id de la mesa es obligatorio")
    private Integer idMesa;
    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fechaHora;
    @NotNull(message = "El teléfono es obligatorio")
    @Min(value = 900000000, message = "El teléfono debe tener 9 dígitos")
    @Max(value = 999999999, message = "El teléfono debe tener 9 dígitos")
    private Integer telefono;
    @NotNull(message = "El estado de la solicitud es obligatorio")
    private Reserva.EstadoSolicitud estadoSolicitud;
    @Size(max = 300, message = "Las notas especiales no pueden tener más de 300 caracteres")
    private String notasEspeciales;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
