package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;
    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 150, message = "El DNI no puede tener más de 150 caracteres")
    private String DNI;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 200, message = "La contraseña debe tener entre 8 y 200 caracteres")
    private String contrasena;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
