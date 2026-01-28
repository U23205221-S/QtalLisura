package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

/**
 *
 * @param <DRQ> request DTO
 * @param <DRE> response DTO
 */
@Transactional
public interface Updatable<DRQ,DRE> {
    DRE updateById(Integer id, DRQ dto);
}
