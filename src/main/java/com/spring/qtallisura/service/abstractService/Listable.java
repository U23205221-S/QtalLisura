package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

import java.util.List;

/**
 *
 * @param <DRE> DTO response de trabajador_servicio
 */
@Transactional
public interface Listable<DRE>{
    List<DRE> allList();
}
