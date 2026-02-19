package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente idCliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa idMesa;

    @Column(name = "codigo", nullable = false, length = 6, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", nullable = false)
    private EstadoPedido estadoPedido;

    @Column(name = "total", nullable = false)
    private Double total;
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @Getter
    @AllArgsConstructor
    public enum EstadoPedido {
        PENDIENTE("Pendiente"),
        EN_PREPARACION("En Preparación"),
        SERVIDO("Servido"),
        PAGADO("Pagado"),
        CANCELADO("Cancelado");
        final String estado;
    }

    @OneToOne(mappedBy = "idPedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Pago pago;
    @OneToMany(mappedBy = "idPedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetallePedido> detallePedidos;
    @OneToMany(mappedBy = "idPedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Resena> resenas;

}
