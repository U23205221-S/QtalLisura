package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.ClienteRequestDTO;
import com.spring.qtallisura.dto.response.ClienteResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClienteMapper extends Convert<Cliente, ClienteRequestDTO, ClienteResponseDTO>,
        UpdatePatch<ClienteRequestDTO, Cliente> {

    @Mapping(target = "estadoBD", source = "estadoBD.nombre")
    @Override
    ClienteResponseDTO toDTO(Cliente model);

    @Override
    Cliente toModel(ClienteRequestDTO dto);
}
