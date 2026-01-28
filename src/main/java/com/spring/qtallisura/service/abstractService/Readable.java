package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

@Transactional
public interface Readable<DRE>{
    DRE readById(Integer id);
}
