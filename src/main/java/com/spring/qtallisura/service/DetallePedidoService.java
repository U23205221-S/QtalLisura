package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.DetallePedidoRequestDTO;
import com.spring.qtallisura.dto.response.DetallePedidoResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.DetallePedidoMapper;
import com.spring.qtallisura.model.DetallePedido;
import com.spring.qtallisura.model.MovimientoInventario;
import com.spring.qtallisura.model.Pedido;
import com.spring.qtallisura.model.Producto;
import com.spring.qtallisura.repository.DetallePedidoRepository;
import com.spring.qtallisura.repository.MovimientoInventarioRepository;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.repository.ProductoRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetallePedidoService implements ServiceAbs<DetallePedidoRequestDTO, DetallePedidoResponseDTO> {

    private final DetallePedidoRepository detallePedidoRepository;
    private final DetallePedidoMapper detallePedidoMapper;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Transactional
    @Override
    public DetallePedidoResponseDTO create(DetallePedidoRequestDTO dto) {
        log.info("DetallePedidoService.create()");

        Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new EServiceLayer("El pedido no existe"));

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new EServiceLayer("El producto no existe"));

        // Validar stock disponible
        if (producto.getStockActual() < dto.getCantidad()) {
            log.warn("Stock insuficiente para producto {}: disponible={}, solicitado={}",
                    producto.getNombre(), producto.getStockActual(), dto.getCantidad());
            throw new EServiceLayer("Stock insuficiente para '" + producto.getNombre() +
                    "'. Disponible: " + producto.getStockActual() + ", Solicitado: " + dto.getCantidad());
        }

        DetallePedido model = detallePedidoMapper.toModel(dto);
        model.setIdPedido(pedido);
        model.setIdProducto(producto);

        // Calcular subtotal
        if (dto.getPrecioUnitario() != null && dto.getCantidad() != null) {
            model.setSubtotal(dto.getPrecioUnitario() * dto.getCantidad());
        }

        model = detallePedidoRepository.save(model);

        // Reducir stock del producto
        int stockAnterior = producto.getStockActual();
        producto.setStockActual(stockAnterior - dto.getCantidad());
        productoRepository.save(producto);
        log.info("Stock actualizado para producto '{}': {} -> {}",
                producto.getNombre(), stockAnterior, producto.getStockActual());

        // Registrar movimiento de inventario (SALIDA)
        registrarMovimientoInventario(producto, dto.getCantidad(), MovimientoInventario.TipoMovimiento.SALIDA);

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

    @Transactional
    public List<DetallePedidoResponseDTO> findByPedido(Integer idPedido) {
        log.info("DetallePedidoService.findByPedido() - Pedido ID: {}", idPedido);
        return detallePedidoRepository.findByIdPedido_IdPedido(idPedido)
                .stream()
                .map(detallePedidoMapper::toDTO)
                .toList();
    }

    /**
     * Devuelve el stock de todos los productos de un pedido.
     * Se usa cuando un pedido es CANCELADO.
     */
    @Transactional
    public void devolverStockPorPedido(Integer idPedido) {
        log.info("DetallePedidoService.devolverStockPorPedido() - Pedido ID: {}", idPedido);

        List<DetallePedido> detalles = detallePedidoRepository.findByIdPedido_IdPedido(idPedido);

        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getIdProducto();
            int stockAnterior = producto.getStockActual();
            int cantidadDevuelta = detalle.getCantidad();

            producto.setStockActual(stockAnterior + cantidadDevuelta);
            productoRepository.save(producto);

            log.info("Stock devuelto para producto '{}': {} -> {} (+{})",
                    producto.getNombre(), stockAnterior, producto.getStockActual(), cantidadDevuelta);

            // Registrar movimiento de inventario (ENTRADA por devolución)
            registrarMovimientoInventario(producto, cantidadDevuelta, MovimientoInventario.TipoMovimiento.ENTRADA);
        }
    }

    /**
     * Registra un movimiento de inventario (ENTRADA o SALIDA)
     */
    private void registrarMovimientoInventario(Producto producto, Integer cantidad,
                                                MovimientoInventario.TipoMovimiento tipo) {
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .idProducto(producto)
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .fechaMovimiento(LocalDateTime.now())
                .build();

        movimientoInventarioRepository.save(movimiento);
        log.info("Movimiento de inventario registrado: {} {} unidades de '{}'",
                tipo.getValue(), cantidad, producto.getNombre());
    }
}
