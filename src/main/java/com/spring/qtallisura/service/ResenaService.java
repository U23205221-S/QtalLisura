package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ResenaRequestDTO;
import com.spring.qtallisura.dto.response.ResenaResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.ResenaMapper;
import com.spring.qtallisura.model.*;
import com.spring.qtallisura.repository.*;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    private final DetallePedidoRepository detallePedidoRepository;

    // DTO interno para devolver info del pedido al buscar por código
    @Data
    @Builder
    @AllArgsConstructor
    public static class PedidoResenaDTO {
        private Integer idPedido;
        private String codigoPedido;
        private List<ProductoResenaDTO> productos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ProductoResenaDTO {
        private Integer idProducto;
        private String nombre;
        private String imagenUrl;
        private boolean yaResenado;
    }

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

        // Solo se puede reseñar pedidos PAGADOS
        if (pedido.getEstadoPedido() != Pedido.EstadoPedido.PAGADO) {
            throw new EServiceLayer("Solo puedes reseñar pedidos que ya han sido pagados");
        }

        // Verificar que el producto pertenece al pedido
        boolean productoEnPedido = detallePedidoRepository
                .findByIdPedido_IdPedido(pedido.getIdPedido())
                .stream()
                .anyMatch(d -> d.getIdProducto().getIdProducto().equals(dto.getIdProducto()));

        if (!productoEnPedido) {
            throw new EServiceLayer("El producto no pertenece a este pedido");
        }

        // Verificar que este cliente no haya reseñado ya este producto en este pedido
        boolean yaResenado = resenaRepository
                .existsByIdPedido_IdPedidoAndIdCliente_IdClienteAndIdProducto_IdProducto(
                        dto.getIdPedido(), dto.getIdCliente(), dto.getIdProducto());

        if (yaResenado) {
            throw new EServiceLayer("Ya has enviado una reseña para este producto en este pedido");
        }

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

    /**
     * Obtener reseñas realizadas por un cliente
     */
    @Transactional
    public List<ResenaResponseDTO> findByCliente(Integer idCliente) {
        log.info("ResenaService.findByCliente() - Cliente ID: {}", idCliente);
        return resenaRepository.findByIdCliente_IdCliente(idCliente)
                .stream()
                .map(resenaMapper::toDTO)
                .toList();
    }

    /**
     * Busca un pedido por código y valida que pertenezca al cliente.
     * Retorna los productos del pedido con su estado de reseña.
     */
    @Transactional
    public PedidoResenaDTO buscarPedidoPorCodigo(String codigo, Integer idCliente) {
        log.info("ResenaService.buscarPedidoPorCodigo() - Código: {}, Cliente: {}", codigo, idCliente);

        // Buscar el pedido por código
        Pedido pedido = pedidoRepository.findByCodigo(codigo.toUpperCase().trim())
                .orElseThrow(() -> new EServiceLayer("No se encontró un pedido con el código: " + codigo));

        // Solo se puede reseñar pedidos PAGADOS
        if (pedido.getEstadoPedido() != Pedido.EstadoPedido.PAGADO) {
            throw new EServiceLayer("Solo puedes reseñar pedidos que ya han sido pagados. Estado actual: "
                    + pedido.getEstadoPedido().getEstado());
        }

        // Obtener los productos del pedido y verificar cuáles ya reseñó ESTE cliente
        List<DetallePedido> detalles = detallePedidoRepository.findByIdPedido_IdPedido(pedido.getIdPedido());

        if (detalles.isEmpty()) {
            throw new EServiceLayer("Este pedido no tiene productos registrados");
        }

        List<ProductoResenaDTO> productos = detalles.stream().map(detalle -> {
            Producto prod = detalle.getIdProducto();
            boolean yaResenado = resenaRepository
                    .existsByIdPedido_IdPedidoAndIdCliente_IdClienteAndIdProducto_IdProducto(
                            pedido.getIdPedido(), idCliente, prod.getIdProducto());
            return ProductoResenaDTO.builder()
                    .idProducto(prod.getIdProducto())
                    .nombre(prod.getNombre())
                    .imagenUrl(prod.getImagenUrl())
                    .yaResenado(yaResenado)
                    .build();
        }).toList();

        return PedidoResenaDTO.builder()
                .idPedido(pedido.getIdPedido())
                .codigoPedido(pedido.getCodigo())
                .productos(productos)
                .build();
    }

    private Resena searchEntityById(Integer id) {
        log.info("ResenaService.searchEntityById()");
        return resenaRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("La reseña no existe"));
    }
}
