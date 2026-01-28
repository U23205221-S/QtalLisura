package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria idCategoria;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "descripcion", nullable = false, length = 250)
    private String descripcion;
    @Column(name = "precio_venta", nullable = false, length = 10)
    private Double precioVenta;
    @Column(name = "imagen_url", length = 250, nullable = false)
    private String imagenUrl;
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;
    @Column(name = "costo_unitario", nullable = false)
    private Double costoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @OneToMany(mappedBy = "idProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DetallePedido> detallePedidos;
    @OneToMany(mappedBy = "idProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Resena> resenas;
    @OneToMany(mappedBy = "idProducto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MovimientoInventario> movimientoInventario;

}
