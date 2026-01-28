package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido idPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "codigo_transaccion", nullable = false, unique = true)
    private UUID codigoTransaccion;
    @Column(name = "monto", nullable = false)
    private Double monto;
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago;

    @Getter
    @AllArgsConstructor
    public enum MetodoPago {
        TARJETA_CREDITO("Tarjeta de Crédito"),
        TARJETA_DEBITO("Tarjeta de Débito"),
        PAYPAL("PayPal"),
        EFECTIVO("Efectivo");
        final String metodo;
    }
    @Getter
    @AllArgsConstructor
    public enum EstadoPago {
        COMPLETADO("Completado"),
        FALLIDO("Fallido");
        final String estado;
    }

}
