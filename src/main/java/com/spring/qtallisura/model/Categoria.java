package com.spring.qtallisura.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    @Column(name = "nombre",nullable = false, length = 100, unique = true)
    private String nombre;
    @Column(name = "descripcion", length = 250, nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bd", nullable = false)
    private EstadoBD estadoBD;

    @OneToMany(mappedBy = "idCategoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;

}
