package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.ReservaRequestDTO;
import com.spring.qtallisura.dto.response.ReservaResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReservaMapper extends Convert<Reserva, ReservaRequestDTO, ReservaResponseDTO>,
        UpdatePatch<ReservaRequestDTO, Reserva> {

    @Mapping(target = "idCliente", source = "idCliente.idCliente")
    @Mapping(target = "clienteNombre", expression = "java(model.getIdCliente().getNombre() + \" \" + model.getIdCliente().getApellido())")
    @Mapping(target = "idMesa", source = "idMesa.idMesa")
    @Mapping(target = "numeroMesa", source = "idMesa.numeroMesa")
    @Mapping(target = "estadoSolicitud", source = "estadoSolicitud.estado")
    @Mapping(target = "estadoBD", source = "estadoBD.nombre")
    @Override
    ReservaResponseDTO toDTO(Reserva model);

    @Mapping(target = "idCliente.idCliente", source = "idCliente")
    @Mapping(target = "idMesa.idMesa", source = "idMesa")
    @Override
    Reserva toModel(ReservaRequestDTO dto);

    @Override
    @Mapping(target = "idCliente.idCliente", source = "idCliente")
    @Mapping(target = "idMesa.idMesa", source = "idMesa")
    void updateFromDto(ReservaRequestDTO dto, @MappingTarget Reserva entity);
}

