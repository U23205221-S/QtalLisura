package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 250, message = "La descripción no puede tener más de 250 caracteres")
    private String descripcion;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
