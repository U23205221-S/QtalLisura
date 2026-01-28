package com.spring.qtallisura.service.abstractService;

import jakarta.transaction.Transactional;

/**
 * Agrupación de métodos que se deben implementar en el servicio del modelo
 * @param <DRE> response DTO
 */
@Transactional
public interface ServiceAbs<DRQ,DRE>
        extends Creatable<DRQ,DRE>,
        Readable<DRE>,
        Updatable<DRQ,DRE>,
        Removable,
        Listable<DRE>{

}
