package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Modulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModulo;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;
    @Column(name = "descripcion", nullable = false, length = 250)
    private String descripcion;

    @OneToMany(mappedBy = "idModulo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PerfilModulo> permisos;

}
