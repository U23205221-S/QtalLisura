package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

/**
 * Método que permite registrar y persistir una entidad en la base de datos.
 * @param <DRQ> request DTO
 * @param <DRE> response DTO
 */
@Transactional
public interface Creatable<DRQ, DRE> {
    DRE create(DRQ dto);
}
