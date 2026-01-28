package com.spring.qtallisura.mapper.convert;

/**
 * Generic interface for converting DTOs to Models.
 *
 * @param <M> Modelo
 * @param <D> DTO
 */

public interface ConvertModel<M, D> {
    M toModel(D dto);
}
