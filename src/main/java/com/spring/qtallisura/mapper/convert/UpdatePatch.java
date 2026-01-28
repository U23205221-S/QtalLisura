package com.spring.qtallisura.mapper.convert;


import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Para que se pueda actualizar un objeto con los valores de un DTO, ya sea completo o parcialmente.
 * @param <DRQ> DTO Request
 * @param <T> Entity
 */
public interface UpdatePatch<DRQ, T> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(DRQ dto, @MappingTarget T entity);
}
