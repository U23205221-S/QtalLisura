package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.DetallePedidoRequestDTO;
import com.spring.qtallisura.dto.response.DetallePedidoResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.DetallePedidoMapper;
import com.spring.qtallisura.model.DetallePedido;
import com.spring.qtallisura.model.Pedido;
import com.spring.qtallisura.model.Producto;
import com.spring.qtallisura.repository.DetallePedidoRepository;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.repository.ProductoRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetallePedidoService implements ServiceAbs<DetallePedidoRequestDTO, DetallePedidoResponseDTO> {

    private final DetallePedidoRepository detallePedidoRepository;
    private final DetallePedidoMapper detallePedidoMapper;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    @Override
    public DetallePedidoResponseDTO create(DetallePedidoRequestDTO dto) {
        log.info("DetallePedidoService.create()");

        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new EServiceLayer("El pedido no existe"));

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new EServiceLayer("El producto no existe"));

        DetallePedido model = detallePedidoMapper.toModel(dto);
        model.setIdPedido(pedido);
        model.setIdProducto(producto);

        // Calcular subtotal
        if (dto.getPrecioUnitario() != null && dto.getCantidad() != null) {
            model.setSubtotal(dto.getPrecioUnitario() * dto.getCantidad());
        }

        model = detallePedidoRepository.save(model);
        return detallePedidoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<DetallePedidoResponseDTO> allList() {
        log.info("DetallePedidoService.allList()");
        return detallePedidoRepository.findAll().stream()
                .map(detallePedidoMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public DetallePedidoResponseDTO readById(Integer id) {
        log.info("DetallePedidoService.readById()");
        DetallePedido model = searchEntityById(id);
        return detallePedidoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("DetallePedidoService.remove()");
        DetallePedido model = searchEntityById(id);
        detallePedidoRepository.delete(model);
    }

    @Transactional
    @Override
    public DetallePedidoResponseDTO updateById(Integer id, DetallePedidoRequestDTO dto) {
        log.info("DetallePedidoService.updateById()");
        DetallePedido model_existente = searchEntityById(id);

        if (dto.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                    .orElseThrow(() -> new EServiceLayer("El pedido no existe"));
            model_existente.setIdPedido(pedido);
        }

        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new EServiceLayer("El producto no existe"));
            model_existente.setIdProducto(producto);
        }

        if (dto.getCantidad() != null) {
            model_existente.setCantidad(dto.getCantidad());
        }

        if (dto.getPrecioUnitario() != null) {
            model_existente.setPrecioUnitario(dto.getPrecioUnitario());
        }

        // Recalcular subtotal
        model_existente.setSubtotal(model_existente.getPrecioUnitario() * model_existente.getCantidad());

        DetallePedido model_actualizado = detallePedidoRepository.save(model_existente);
        return detallePedidoMapper.toDTO(model_actualizado);
    }

    private DetallePedido searchEntityById(Integer id){
        log.info("DetallePedidoService.searchEntityById()");
        return detallePedidoRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("El detalle de pedido no existe"));
    }
}
