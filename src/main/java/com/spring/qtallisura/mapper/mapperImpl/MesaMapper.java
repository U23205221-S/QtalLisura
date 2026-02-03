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

    @Mapping(target = "estadoMesa", source = "estadoMesa.estado")
    @Mapping(target = "ubicacion", source = "ubicacion.ubicacion")
    @Mapping(target = "estadoBD", source = "estadoBD.nombre")
    @Override
    MesaResponseDTO toDTO(Mesa model);

    @Override
    Mesa toModel(MesaRequestDTO dto);
}
