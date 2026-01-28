package com.spring.qtallisura.dto.request;

import com.spring.qtallisura.model.EstadoBD;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductoRequestDTO {

    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 250, message = "La descripción no puede tener más de 250 caracteres")
    private String descripcion;
    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser un valor positivo")
    @Digits(integer = 10, fraction = 2, message = "El precio de venta debe tener como máximo 10 dígitos enteros y 2 decimales")
    private Double precioVenta;
    @NotNull(message = "El costo unitario es obligatorio")
    @Positive(message = "El costo unitario debe ser un valor positivo")
    @Digits(integer = 10, fraction = 2, message = "El costo unitario debe tener como máximo 10 dígitos enteros y 2 decimales")
    private Double costoUnitario;
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 250, message = "La URL de la imagen no puede tener más de 250 caracteres")
    private String imagenUrl;
    @NotNull(message = "El estado es obligatorio")
    private EstadoBD estadoBD;

}
