package com.spring.qtallisura.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoResponseDTO {

    private Integer idProducto;
    private String categoriaNombre;
    private String nombre;
    private String descripcion;
    private Double precioVenta;
    private Double costoUnitario;
    private Integer stockActual;
    private Integer stockMinimo;
    private String imagenUrl;
    private String estadoBD;

}
