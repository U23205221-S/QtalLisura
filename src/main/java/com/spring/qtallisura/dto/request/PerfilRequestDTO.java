package com.spring.qtallisura.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PerfilRequestDTO {

    @NotBlank(message = "El nombre del perfil no puede estar vacío")
    @Size(max = 100, message = "El nombre del perfil no puede tener más de 100 caracteres")
    private String nombrePerfil;
    @NotBlank(message = "La descripción del perfil no puede estar vacía")
    @Size(max = 250, message = "La descripción del perfil no puede tener más de 250 caracteres")
    private String descripcionPerfil;

}
