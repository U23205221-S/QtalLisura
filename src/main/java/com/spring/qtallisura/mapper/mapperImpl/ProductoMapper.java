package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.ProductoRequestDTO;
import com.spring.qtallisura.dto.response.ProductoResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductoMapper extends Convert<Producto, ProductoRequestDTO, ProductoResponseDTO>,
        UpdatePatch<ProductoRequestDTO ,Producto > {

    @Mapping(target = "categoriaNombre", source = "idCategoria.nombre")
    @Mapping(target = "estadoBD", expression = "java(model.getEstadoBD().name())")
    @Override
    ProductoResponseDTO toDTO(Producto model);

    @Mapping(target = "idCategoria.idCategoria", source = "idCategoria")
    @Override
    Producto toModel(ProductoRequestDTO dto);

    @Override
    @Mapping(target = "idCategoria.idCategoria", source = "idCategoria")
    void updateFromDto(ProductoRequestDTO dto, @MappingTarget Producto entity);

}
