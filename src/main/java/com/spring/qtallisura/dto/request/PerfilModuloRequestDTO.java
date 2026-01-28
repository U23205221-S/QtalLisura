package com.spring.qtallisura.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PerfilModuloRequestDTO {

    @NotNull(message = "El id del perfil no puede ser nulo")
    private Integer idPerfil;
    @NotNull(message = "El id del módulo no puede ser nulo")
    private Integer idModulo;

}
