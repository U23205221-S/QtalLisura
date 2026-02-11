package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.CategoriaRequestDTO;
import com.spring.qtallisura.dto.response.CategoriaResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoriaMapper extends Convert<Categoria, CategoriaRequestDTO, CategoriaResponseDTO>,
        UpdatePatch<CategoriaRequestDTO ,Categoria > {

    @Mapping(target = "estadoBD", expression = "java(model.getEstadoBD().name())")
    @Override
    CategoriaResponseDTO toDTO(Categoria model);

    @Override
    Categoria toModel(CategoriaRequestDTO dto);
}
