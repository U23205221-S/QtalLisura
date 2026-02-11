package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.MesaRequestDTO;
import com.spring.qtallisura.dto.response.MesaResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Mesa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MesaMapper extends Convert<Mesa, MesaRequestDTO, MesaResponseDTO>,
        UpdatePatch<MesaRequestDTO, Mesa> {

    @Mapping(target = "estadoMesa", expression = "java(model.getEstadoMesa().name())")
    @Mapping(target = "ubicacion", expression = "java(model.getUbicacion().name())")
    @Mapping(target = "estadoBD", expression = "java(model.getEstadoBD().name())")
    @Override
    MesaResponseDTO toDTO(Mesa model);

    @Override
    Mesa toModel(MesaRequestDTO dto);
}
