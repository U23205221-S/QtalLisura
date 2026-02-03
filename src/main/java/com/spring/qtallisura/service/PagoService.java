package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.PagoRequestDTO;
import com.spring.qtallisura.dto.response.PagoResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.PagoMapper;
import com.spring.qtallisura.model.Pago;
import com.spring.qtallisura.model.Pedido;
import com.spring.qtallisura.repository.PagoRepository;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PagoService implements ServiceAbs<PagoRequestDTO, PagoResponseDTO> {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final PedidoRepository pedidoRepository;

    @Transactional
    @Override
    public PagoResponseDTO create(PagoRequestDTO dto) {
        log.info("PagoService.create()");

        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new EServiceLayer("El pedido no existe"));

        Pago model = pagoMapper.toModel(dto);
        model.setIdPedido(pedido);

        model = pagoRepository.save(model);
        return pagoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<PagoResponseDTO> allList() {
        log.info("PagoService.allList()");
        return pagoRepository.findAll().stream()
                .map(pagoMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public PagoResponseDTO readById(Integer id) {
        log.info("PagoService.readById()");
        Pago model = searchEntityById(id);
        return pagoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("PagoService.remove()");
        Pago model = searchEntityById(id);
        pagoRepository.delete(model);
    }

    @Transactional
    @Override
    public PagoResponseDTO updateById(Integer id, PagoRequestDTO dto) {
        log.info("PagoService.updateById()");
        Pago model_existente = searchEntityById(id);

        if (dto.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                    .orElseThrow(() -> new EServiceLayer("El pedido no existe"));
            model_existente.setIdPedido(pedido);
        }

        if (dto.getMetodoPago() != null) {
            model_existente.setMetodoPago(dto.getMetodoPago());
        }

        if (dto.getCodigoTransaccion() != null) {
            model_existente.setCodigoTransaccion(dto.getCodigoTransaccion());
        }

        if (dto.getMonto() != null) {
            model_existente.setMonto(dto.getMonto());
        }

        if (dto.getFechaPago() != null) {
            model_existente.setFechaPago(dto.getFechaPago());
        }

        if (dto.getEstadoPago() != null) {
            model_existente.setEstadoPago(dto.getEstadoPago());
        }

        Pago model_actualizado = pagoRepository.save(model_existente);
        return pagoMapper.toDTO(model_actualizado);
    }

    private Pago searchEntityById(Integer id){
        log.info("PagoService.searchEntityById()");
        return pagoRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("El pago no existe"));
    }
}
