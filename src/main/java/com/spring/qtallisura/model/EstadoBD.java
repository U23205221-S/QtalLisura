package com.spring.qtallisura.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoBD {

    ACTIVO(1, "Activo"),
    INACTIVO(2, "Inactivo"),
    ELIMINADO(0, "Eliminado");

    final int id;
    final String nombre;

}
