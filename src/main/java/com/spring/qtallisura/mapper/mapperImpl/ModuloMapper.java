package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.ModuloRequestDTO;
import com.spring.qtallisura.dto.response.ModuloResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Modulo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ModuloMapper extends Convert<Modulo, ModuloRequestDTO, ModuloResponseDTO>,
        UpdatePatch< ModuloRequestDTO , Modulo> {

    @Override
    ModuloResponseDTO toDTO(Modulo modulo);

    @Override
    Modulo toModel(ModuloRequestDTO dto);

}
