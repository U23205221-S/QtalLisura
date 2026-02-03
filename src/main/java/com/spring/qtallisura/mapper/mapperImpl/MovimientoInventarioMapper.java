package com.spring.qtallisura.mapper.mapperImpl;

import com.spring.qtallisura.dto.request.MovimientoInventarioRequestDTO;
import com.spring.qtallisura.dto.response.MovimientoInventarioResponseDTO;
import com.spring.qtallisura.mapper.convert.Convert;
import com.spring.qtallisura.mapper.convert.UpdatePatch;
import com.spring.qtallisura.model.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovimientoInventarioMapper extends Convert<MovimientoInventario, MovimientoInventarioRequestDTO, MovimientoInventarioResponseDTO>,
        UpdatePatch<MovimientoInventarioRequestDTO, MovimientoInventario> {

    @Mapping(target = "productoNombre", source = "idProducto.nombre")
    @Mapping(target = "tipoMovimiento", source = "tipoMovimiento.value")
    @Override
    MovimientoInventarioResponseDTO toDTO(MovimientoInventario model);

    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    @Override
    MovimientoInventario toModel(MovimientoInventarioRequestDTO dto);

    @Override
    @Mapping(target = "idProducto.idProducto", source = "idProducto")
    void updateFromDto(MovimientoInventarioRequestDTO dto, @MappingTarget MovimientoInventario entity);
}

