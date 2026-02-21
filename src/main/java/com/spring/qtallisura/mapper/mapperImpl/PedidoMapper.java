package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.PedidoRequestDTO;
import com.spring.qtallisura.dto.response.PedidoResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PedidoMapper extends Convert<Pedido, PedidoRequestDTO, PedidoResponseDTO>,
        UpdatePatch<PedidoRequestDTO, Pedido> {

    @Mapping(target = "clienteNombre", expression = "java(model.getIdCliente() != null ? model.getIdCliente().getNombre() + \" \" + model.getIdCliente().getApellido() : \"Consumidor Final\")")
    @Mapping(target = "usuarioNombre", expression = "java(model.getIdUsuario().getNombres() + \" \" + model.getIdUsuario().getApellidos())")
    @Mapping(target = "numeroMesa", source = "idMesa.numeroMesa")
    @Mapping(target = "estadoPedido", source = "estadoPedido.estado")
    @Mapping(target = "estadoBD", source = "estadoBD.nombre")
    @Override
    PedidoResponseDTO toDTO(Pedido model);

    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "idMesa", ignore = true)
    @Override
    Pedido toModel(PedidoRequestDTO dto);

    @Override
    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "idMesa", ignore = true)
    void updateFromDto(PedidoRequestDTO dto, @MappingTarget Pedido entity);
}

