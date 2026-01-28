package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellido;

    @Column(name = "DNI", nullable = false, length = 150, unique = true)
    private String DNI;

    @Column(name = "contrasena", nullable = false, length = 200)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @OneToMany(mappedBy = "idCliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservas;
    @OneToMany(mappedBy = "idCliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;
    @OneToMany(mappedBy = "idCliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resena> resenas;

}
