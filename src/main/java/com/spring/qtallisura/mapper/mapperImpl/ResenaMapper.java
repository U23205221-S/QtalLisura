package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.ResenaRequestDTO;
import com.spring.qtallisura.dto.response.ResenaResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Resena;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResenaMapper extends Convert<Resena, ResenaRequestDTO, ResenaResponseDTO>,
        UpdatePatch<ResenaRequestDTO, Resena> {

    @Mapping(target = "clienteNombre", expression = "java(model.getIdCliente().getNombre() + \" \" + model.getIdCliente().getApellido())")
    @Mapping(target = "productoNombre", source = "idProducto.nombre")
    @Mapping(target = "codigoPedido", source = "idPedido.codigo")
    @Override
    ResenaResponseDTO toDTO(Resena model);

    @Mapping(target = "idCliente.idCliente", source = "idCliente")
    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    @Override
    Resena toModel(ResenaRequestDTO dto);

    @Override
    @Mapping(target = "idCliente.idCliente", source = "idCliente")
    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    void updateFromDto(ResenaRequestDTO dto, @MappingTarget Resena entity);
}
