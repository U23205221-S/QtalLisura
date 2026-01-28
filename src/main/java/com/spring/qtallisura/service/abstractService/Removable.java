package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

@Transactional
public interface Removable{
    void remove(Integer id);
}
