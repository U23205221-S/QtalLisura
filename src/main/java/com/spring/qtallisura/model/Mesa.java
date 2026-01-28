package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Mesa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMesa;

    @Column(name = "numero_mesa", nullable = false, length = 2, unique = true)
    private Integer numeroMesa;

    @Column(name = "capacidad_maxima", nullable = false, length = 2)
    private Integer capacidadMaxima;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_mesa", nullable = false)
    private EstadoMesa estadoMesa;

    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion", nullable = false)
    private Ubicacion ubicacion;

    @Getter
    @AllArgsConstructor
    public enum EstadoMesa {
        DISPONIBLE ("Disponible"),
        OCUPADA ("Ocupada"),
        RESERVADA ("Reservada"),
        EN_MANTENIMIENTO ("En Mantenimiento");
        final String estado;
    }
    @Getter
    @AllArgsConstructor
    public enum Ubicacion{
       PRIMER_PISO("Primer Piso"),
        SEGUNDO_PISO("Segundo Piso");
        final String ubicacion;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @OneToMany(mappedBy = "idMesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservas;
    @OneToMany(mappedBy = "idMesa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

}
