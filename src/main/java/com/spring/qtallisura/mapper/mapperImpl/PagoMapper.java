package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.PagoRequestDTO;
import com.spring.qtallisura.dto.response.PagoResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PagoMapper extends Convert<Pago, PagoRequestDTO, PagoResponseDTO>,
        UpdatePatch<PagoRequestDTO, Pago> {

    @Mapping(target = "idPedido", source = "idPedido.idPedido")
    @Mapping(target = "metodoPago", source = "metodoPago.metodo")
    @Mapping(target = "estadoPago", source = "estadoPago.estado")
    @Override
    PagoResponseDTO toDTO(Pago model);

    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    @Override
    Pago toModel(PagoRequestDTO dto);

    @Override
    @Mapping(target = "idPedido.idPedido", source = "idPedido")
    void updateFromDto(PagoRequestDTO dto, @MappingTarget Pago entity);
}

