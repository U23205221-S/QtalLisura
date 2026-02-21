package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Detalle_Pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetallePedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido idPedido;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;

    @Column(name = "cantidad", nullable = false, length = 2)
    private Integer cantidad;
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;
    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

}
