package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.Pago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PagoRequestDTO {

    @NotNull(message = "El id del pedido es obligatorio")
    private Integer idPedido;
    @NotNull(message = "El método de pago es obligatorio")
    private Pago.MetodoPago metodoPago;
    @NotNull(message = "El código de transacción es obligatorio")
    private UUID codigoTransaccion;
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser un valor positivo")
    private Double monto;
    @NotNull(message = "La fecha de pago es obligatoria")
    private LocalDateTime fechaPago;
    @NotNull(message = "El estado del pago es obligatorio")
    private Pago.EstadoPago estadoPago;

}
