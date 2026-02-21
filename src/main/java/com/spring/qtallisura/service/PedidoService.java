package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.PedidoRequestDTO;
import com.spring.qtallisura.dto.response.PedidoResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.PedidoMapper;
import com.spring.qtallisura.model.*;
import com.spring.qtallisura.repository.ClienteRepository;
import com.spring.qtallisura.repository.MesaRepository;
import com.spring.qtallisura.repository.PedidoRepository;
import com.spring.qtallisura.repository.UsuarioRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoService implements ServiceAbs<PedidoRequestDTO, PedidoResponseDTO> {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MesaRepository mesaRepository;
    private final DetallePedidoService detallePedidoService;

    @Transactional
    @Override
    public PedidoResponseDTO create(PedidoRequestDTO dto) {
        log.info("PedidoService.create()");

        if (pedidoRepository.existsByCodigo(dto.getCodigo())){
            log.warn("PedidoService.create() - El código {} ya está registrado",
                    dto.getCodigo());
            throw new EServiceLayer("El código del pedido ya está registrado en el sistema");
        }

        Cliente cliente = null;
        if (dto.getIdCliente() != null) {
            cliente = clienteRepository.findById(dto.getIdCliente())
                    .orElseThrow(() -> new EServiceLayer("El cliente no existe"));
        }

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EServiceLayer("El usuario no existe"));

        Mesa mesa = mesaRepository.findById(dto.getIdMesa())
                .orElseThrow(() -> new EServiceLayer("La mesa no existe"));

        if (mesa.getEstadoMesa() != Mesa.EstadoMesa.DISPONIBLE) {
            throw new EServiceLayer("La mesa no está disponible. Estado actual: " + mesa.getEstadoMesa().getEstado());
        }

        Pedido model = pedidoMapper.toModel(dto);
        model.setIdCliente(cliente);
        model.setIdUsuario(usuario);
        model.setIdMesa(mesa);
        model.setEstadoBD(EstadoBD.ACTIVO);

        model = pedidoRepository.save(model);

        // Marcar mesa como OCUPADA
        mesa.setEstadoMesa(Mesa.EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);

        return pedidoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<PedidoResponseDTO> allList() {
        log.info("PedidoService.allList()");
        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(pedidoMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<PedidoResponseDTO> findByUsuario(Integer idUsuario) {
        log.info("PedidoService.findByUsuario() - idUsuario: {}", idUsuario);
        return pedidoRepository.findByIdUsuario_IdUsuario(idUsuario).stream()
                .filter(pedido -> pedido.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(pedidoMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public PedidoResponseDTO readById(Integer id) {
        log.info("PedidoService.readById()");
        Pedido model = searchEntityById(id);
        return pedidoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("PedidoService.remove()");
        Pedido model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        pedidoRepository.save(model);
    }

    @Transactional
    @Override
    public PedidoResponseDTO updateById(Integer id, PedidoRequestDTO dto) {
        log.info("PedidoService.updateById()");
        Pedido model_existente = searchEntityById(id);

        if (dto.getCodigo() != null && !model_existente.getCodigo().equals(dto.getCodigo())
                && pedidoRepository.existsByCodigo(dto.getCodigo())){
            log.warn("PedidoService.updateById() - El código {} ya está registrado",
                    dto.getCodigo());
            throw new EServiceLayer("El código del pedido ya está registrado en el sistema");
        }

        if (dto.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                    .orElseThrow(() -> new EServiceLayer("El cliente no existe"));
            model_existente.setIdCliente(cliente);
        }

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EServiceLayer("El usuario no existe"));
            model_existente.setIdUsuario(usuario);
        }

        if (dto.getIdMesa() != null) {
            Mesa mesa = mesaRepository.findById(dto.getIdMesa())
                    .orElseThrow(() -> new EServiceLayer("La mesa no existe"));
            model_existente.setIdMesa(mesa);
        }

        if (dto.getCodigo() != null) {
            model_existente.setCodigo(dto.getCodigo());
        }

        if (dto.getEstadoPedido() != null) {
            // Validar transición de estado
            validarTransicionEstado(model_existente.getEstadoPedido(), dto.getEstadoPedido());

            // Si el pedido se cancela, devolver el stock de todos los productos
            if (dto.getEstadoPedido() == Pedido.EstadoPedido.CANCELADO) {
                log.info("Pedido {} cancelado - devolviendo stock de productos", id);
                detallePedidoService.devolverStockPorPedido(id);
            }

            model_existente.setEstadoPedido(dto.getEstadoPedido());
            // Liberar mesa si el pedido pasa a PAGADO o CANCELADO
            if (dto.getEstadoPedido() == Pedido.EstadoPedido.PAGADO
                    || dto.getEstadoPedido() == Pedido.EstadoPedido.CANCELADO) {
                Mesa mesaActual = model_existente.getIdMesa();
                if (mesaActual != null) {
                    mesaActual.setEstadoMesa(Mesa.EstadoMesa.DISPONIBLE);
                    mesaRepository.save(mesaActual);
                }
            }
        }

        if (dto.getTotal() != null) {
            model_existente.setTotal(dto.getTotal());
        }

        if (dto.getFechaPedido() != null) {
            model_existente.setFechaPedido(dto.getFechaPedido());
        }

        if (dto.getEstadoBD() != null) {
            model_existente.setEstadoBD(dto.getEstadoBD());
        }

        Pedido model_actualizado = pedidoRepository.save(model_existente);
        return pedidoMapper.toDTO(model_actualizado);
    }

    private Pedido searchEntityById(Integer id){
        log.info("PedidoService.searchEntityById()");
        return pedidoRepository.findById(id)
                .filter(entity -> entity.getEstadoBD() != EstadoBD.ELIMINADO)
                .orElseThrow(
                        () -> {
                            if (pedidoRepository.findById(id).isPresent()){
                                throw new EServiceLayer("El pedido no existe");
                            } else {
                                return new EServiceLayer("El pedido no se encontró");
                            }
                        }
                );
    }

    /**
     * Valida que la transición de estado sea válida según el flujo de negocio:
     * PENDIENTE -> EN_PREPARACION -> SERVIDO -> PAGADO
     * En cualquier estado se puede pasar a CANCELADO (excepto PAGADO)
     */
    private void validarTransicionEstado(Pedido.EstadoPedido estadoActual, Pedido.EstadoPedido nuevoEstado) {
        log.info("Validando transición de estado: {} -> {}", estadoActual, nuevoEstado);

        // Si el estado no cambia, no hay nada que validar
        if (estadoActual == nuevoEstado) {
            return;
        }

        // No se puede cambiar el estado de un pedido PAGADO
        if (estadoActual == Pedido.EstadoPedido.PAGADO) {
            throw new EServiceLayer("No se puede cambiar el estado de un pedido que ya fue pagado");
        }

        // No se puede cambiar el estado de un pedido CANCELADO
        if (estadoActual == Pedido.EstadoPedido.CANCELADO) {
            throw new EServiceLayer("No se puede cambiar el estado de un pedido cancelado");
        }

        // Se puede cancelar desde cualquier estado (excepto PAGADO que ya se validó arriba)
        if (nuevoEstado == Pedido.EstadoPedido.CANCELADO) {
            return;
        }

        // Validar transiciones válidas según el flujo
        switch (estadoActual) {
            case PENDIENTE:
                if (nuevoEstado != Pedido.EstadoPedido.EN_PREPARACION) {
                    throw new EServiceLayer(
                        "Un pedido PENDIENTE solo puede pasar a EN_PREPARACION");
                }
                break;

            case EN_PREPARACION:
                if (nuevoEstado != Pedido.EstadoPedido.SERVIDO) {
                    throw new EServiceLayer(
                        "Un pedido EN_PREPARACION solo puede pasar a SERVIDO");
                }
                break;

            case SERVIDO:
                if (nuevoEstado != Pedido.EstadoPedido.PAGADO) {
                    throw new EServiceLayer(
                        "Un pedido SERVIDO solo puede pasar a PAGADO");
                }
                break;

            default:
                throw new EServiceLayer("Estado actual no válido: " + estadoActual);
        }

        log.info("Transición de estado válida: {} -> {}", estadoActual, nuevoEstado);
    }
}
