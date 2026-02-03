package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.DetallePedidoRequestDTO;
import com.spring.qtallisura.dto.response.DetallePedidoResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.DetallePedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DetallePedidoMapper extends Convert<DetallePedido, DetallePedidoRequestDTO, DetallePedidoResponseDTO>,
        UpdatePatch<DetallePedidoRequestDTO, DetallePedido> {

    @Mapping(target = "idPedido", source = "idPedido.idPedido")
    @Mapping(target = "productoNombre", source = "idProducto.nombre")
    @Override
    DetallePedidoResponseDTO toDTO(DetallePedido model);

    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    @Override
    DetallePedido toModel(DetallePedidoRequestDTO dto);

    @Override
    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    void updateFromDto(DetallePedidoRequestDTO dto, @MappingTarget DetallePedido entity);
}

