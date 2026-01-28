package com.spring.qtallisura.mapper.convert;

/**
 * Interface that combines ConvertModel and ConvertDTO interfaces for bidirectional conversion.
 *
 * @param <M>   Modelo
 * @param <DRQ> DTO REQUEST.
 * @param <DRE> DTO RESPONSE.
 */

public interface Convert<M, DRQ, DRE> extends ConvertModel<M, DRQ>, ConvertDTO<M, DRE> {

}
