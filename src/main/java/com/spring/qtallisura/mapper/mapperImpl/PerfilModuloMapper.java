package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.PerfilModuloRequestDTO;
import com.spring.qtallisura.dto.response.PerfilModuloResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.PerfilModulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PerfilModuloMapper extends Convert<PerfilModulo, PerfilModuloRequestDTO, PerfilModuloResponseDTO>,
        UpdatePatch<PerfilModuloRequestDTO ,PerfilModulo > {

    @Mapping(target = "perfilNombre", source = "idPerfil.nombre")
    @Mapping(target = "moduloNombre", source = "idModulo.nombre")
    @Override
    PerfilModuloResponseDTO toDTO(PerfilModulo model);

    @Mapping(target = "idPerfil.idPerfil", source = "idPerfil")
    @Mapping(target = "idModulo.idModulo", source = "idModulo")
    @Override
    PerfilModulo toModel(PerfilModuloRequestDTO dto);

    @Override
    @Mapping(target = "idPerfil.idPerfil", source = "idPerfil")
    @Mapping(target = "idModulo.idModulo", source = "idModulo")
    void updateFromDto(PerfilModuloRequestDTO dto, @MappingTarget PerfilModulo entity);

}
