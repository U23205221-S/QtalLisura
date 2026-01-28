package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.UsuarioRequestDTO;
import com.spring.qtallisura.dto.response.UsuarioResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper extends Convert<Usuario, UsuarioRequestDTO, UsuarioResponseDTO>,
        UpdatePatch< UsuarioRequestDTO, Usuario > {

    @Mapping(target = "nombrePerfil", source = "idPerfil.nombre")
    @Mapping(target = "estadoBD", source = "estadoBD.nombre")
    @Override
    UsuarioResponseDTO toDTO(Usuario model);

    @Mapping(target = "nombres", source = "nombresUsuario")
    @Mapping(target = "apellidos", source = "apellidosUsuario")
    @Mapping(target = "idPerfil.idPerfil", source = "idPerfil")
    @Override
    Usuario toModel(UsuarioRequestDTO dto);

    @Override
    @Mapping(target = "idPerfil.idPerfil", source = "idPerfil")
    void updateFromDto(UsuarioRequestDTO dto, @MappingTarget Usuario entity);

}
