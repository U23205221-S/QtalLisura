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
public class UsuarioRequestDTO {

    @NotBlank(message = "Los nombres del usuario no pueden estar vacíos")
    @Size(max = 100, message = "Los nombres del usuario no pueden tener más de 100 caracteres")
    private String nombresUsuario;
    @NotBlank(message = "Los apellidos del usuario no pueden estar vacíos")
    @Size(max = 100, message = "Los apellidos del usuario no pueden tener más de 100 caracteres")
    private String apellidosUsuario;
    @NotBlank(message = "El DNI no puede estar vacío")
    @Size(min = 8, max = 8, message = "El DNI debe de tener exactamente 8 caracteres")
    private String DNI;
    @NotBlank(message = "El usuarname no puede estar vacío")
    @Size(max = 50, message = "El username no puede tener más de 50 caracteres")
    private String username;
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;
    @NotNull(message = "El id del perfil no puede ser nulo")
    private Integer idPerfil;
    @Size(max = 300, message = "La URL de la imagen no puede tener más de 300 caracteres")
    private String imagenUrl;
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoBD estadoBD;

}
