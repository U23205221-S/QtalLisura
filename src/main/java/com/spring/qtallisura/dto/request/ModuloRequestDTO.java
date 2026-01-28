package com.spring.qtallisura.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModuloRequestDTO {

    @NotNull(message = "El nombre del módulo no puede ser nulo")
    @Size(max = 100, message = "El nombre del módulo no puede tener más de 100 caracteres")
    private String nombre;
    @NotNull(message = "La descripción del módulo no puede ser nula")
    @Size(max = 250, message = "La descripción del módulo no puede tener más de 250 caracteres")
    private String descripcion;

}
