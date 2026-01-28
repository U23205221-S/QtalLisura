package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente idCliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa idMesa;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    @Column(name = "telefono", length = 9, nullable = false)
    private Integer telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_solicitud", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Getter
    @AllArgsConstructor
    public enum EstadoSolicitud {
        PENDIENTE("Pendiente"),
        CONFIRMADA("Confirmada"),
        CANCELADA("Cancelada");
        final String estado;
    }

    @Column(name = "notas_especiales", length = 300, nullable = true)
    private String notasEspeciales;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

}
