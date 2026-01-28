package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.PerfilRequestDTO;
import com.spring.qtallisura.dto.response.PerfilResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Perfil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PerfilMapper extends Convert<Perfil, PerfilRequestDTO, PerfilResponseDTO>,
        UpdatePatch< PerfilRequestDTO , Perfil > {

    @Mapping(target = "nombrePerfil", source = "nombre")
    @Mapping(target = "descripcionPerfil", source = "descripcion")
    @Override
    PerfilResponseDTO toDTO(Perfil model);

    @Mapping(target = "nombre", source = "nombrePerfil")
    @Mapping(target = "descripcion", source = "descripcionPerfil")
    @Override
    Perfil toModel(PerfilRequestDTO dto);

}
