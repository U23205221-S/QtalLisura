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
}
