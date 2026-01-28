package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    @Column(name = "DNI", nullable = false, length = 8, unique = true)
    private String DNI;
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;
    @Column(name = "contrasena", nullable = false, length = 100)
    private String contrasena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil idPerfil;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

}
