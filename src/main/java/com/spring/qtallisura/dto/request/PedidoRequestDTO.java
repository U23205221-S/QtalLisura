package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Pedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PedidoRequestDTO {

    private Integer idCliente;
    @NotNull(message = "El id del usuario es obligatorio")
    private Integer idUsuario;
    @NotNull(message = "El id de la mesa es obligatorio")
    private Integer idMesa;
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 6, message = "El código no puede tener más de 6 caracteres")
    private String codigo;
    @NotNull(message = "El estado del pedido es obligatorio")
    private Pedido.EstadoPedido estadoPedido;
    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser un valor positivo")
    private Double total;
    @NotNull(message = "La fecha del pedido es obligatoria")
    private LocalDateTime fechaPedido;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
