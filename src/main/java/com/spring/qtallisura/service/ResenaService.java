package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ResenaRequestDTO;
import com.spring.qtallisura.dto.response.ResenaResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.ResenaMapper;
import com.spring.qtallisura.model.Cliente;
import com.spring.qtallisura.model.Pedido;
import com.spring.qtallisura.model.Producto;
import com.spring.qtallisura.model.Resena;
import com.spring.qtallisura.repository.ClienteRepository;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.repository.ProductoRepository;
import com.spring.qtallisura.repository.ResenaRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResenaService implements ServiceAbs<ResenaRequestDTO, ResenaResponseDTO> {

    private final ResenaRepository resenaRepository;
    private final ResenaMapper resenaMapper;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    @Override
    public ResenaResponseDTO create(ResenaRequestDTO dto) {
        log.info("ResenaService.create()");

        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new EServiceLayer("El cliente no existe"));

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new EServiceLayer("El producto no existe"));

        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new EServiceLayer("El pedido no existe"));

        Resena model = resenaMapper.toModel(dto);
        model.setIdCliente(cliente);
        model.setIdProducto(producto);
        model.setIdPedido(pedido);

        model = resenaRepository.save(model);
        return resenaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<ResenaResponseDTO> allList() {
        log.info("ResenaService.allList()");
        return resenaRepository.findAll().stream()
                .map(resenaMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public ResenaResponseDTO readById(Integer id) {
        log.info("ResenaService.readById()");
        Resena model = searchEntityById(id);
        return resenaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("ResenaService.remove()");
        Resena model = searchEntityById(id);
        resenaRepository.delete(model);
    }

    @Transactional
    @Override
    public ResenaResponseDTO updateById(Integer id, ResenaRequestDTO dto) {
        log.info("ResenaService.updateById()");
        Resena model_existente = searchEntityById(id);

        if (dto.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                    .orElseThrow(() -> new EServiceLayer("El cliente no existe"));
            model_existente.setIdCliente(cliente);
        }

        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new EServiceLayer("El producto no existe"));
            model_existente.setIdProducto(producto);
        }

        if (dto.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                    .orElseThrow(() -> new EServiceLayer("El pedido no existe"));
            model_existente.setIdPedido(pedido);
        }

        if (dto.getCalificacion() != null) {
            model_existente.setCalificacion(dto.getCalificacion());
        }

        if (dto.getComentario() != null) {
            model_existente.setComentario(dto.getComentario());
        }

        if (dto.getFechaResena() != null) {
            model_existente.setFechaResena(dto.getFechaResena());
        }

        Resena model_actualizado = resenaRepository.save(model_existente);
        return resenaMapper.toDTO(model_actualizado);
    }

    private Resena searchEntityById(Integer id){
        log.info("ResenaService.searchEntityById()");
        return resenaRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("La reseña no existe"));
    }
}
