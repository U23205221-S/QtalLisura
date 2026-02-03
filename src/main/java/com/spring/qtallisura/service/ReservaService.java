package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ReservaRequestDTO;
import com.spring.qtallisura.dto.response.ReservaResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.ReservaMapper;
import com.spring.qtallisura.model.Cliente;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Mesa;
import com.spring.qtallisura.model.Reserva;
import com.spring.qtallisura.repository.ClienteRepository;
import com.spring.qtallisura.repository.MesaRepository;
import com.spring.qtallisura.repository.ReservaRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaService implements ServiceAbs<ReservaRequestDTO, ReservaResponseDTO> {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;

    @Transactional
    @Override
    public ReservaResponseDTO create(ReservaRequestDTO dto) {
        log.info("ReservaService.create()");

        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new EServiceLayer("El cliente no existe"));

        Mesa mesa = mesaRepository.findById(dto.getIdMesa())
                .orElseThrow(() -> new EServiceLayer("La mesa no existe"));

        Reserva model = reservaMapper.toModel(dto);
        model.setIdCliente(cliente);
        model.setIdMesa(mesa);
        model.setEstadoBD(EstadoBD.ACTIVO);

        model = reservaRepository.save(model);
        return reservaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<ReservaResponseDTO> allList() {
        log.info("ReservaService.allList()");
        return reservaRepository.findAll().stream()
                .filter(reserva -> reserva.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(reservaMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public ReservaResponseDTO readById(Integer id) {
        log.info("ReservaService.readById()");
        Reserva model = searchEntityById(id);
        return reservaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("ReservaService.remove()");
        Reserva model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        reservaRepository.save(model);
    }

    @Transactional
    @Override
    public ReservaResponseDTO updateById(Integer id, ReservaRequestDTO dto) {
        log.info("ReservaService.updateById()");
        Reserva model_existente = searchEntityById(id);

        if (dto.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                    .orElseThrow(() -> new EServiceLayer("El cliente no existe"));
            model_existente.setIdCliente(cliente);
        }

        if (dto.getIdMesa() != null) {
            Mesa mesa = mesaRepository.findById(dto.getIdMesa())
                    .orElseThrow(() -> new EServiceLayer("La mesa no existe"));
            model_existente.setIdMesa(mesa);
        }

        if (dto.getFechaHora() != null) {
            model_existente.setFechaHora(dto.getFechaHora());
        }

        if (dto.getTelefono() != null) {
            model_existente.setTelefono(dto.getTelefono());
        }

        if (dto.getEstadoSolicitud() != null) {
            model_existente.setEstadoSolicitud(dto.getEstadoSolicitud());
        }

        if (dto.getNotasEspeciales() != null) {
            model_existente.setNotasEspeciales(dto.getNotasEspeciales());
        }

        if (dto.getEstadoBD() != null) {
            model_existente.setEstadoBD(dto.getEstadoBD());
        }

        Reserva model_actualizado = reservaRepository.save(model_existente);
        return reservaMapper.toDTO(model_actualizado);
    }

    private Reserva searchEntityById(Integer id){
        log.info("ReservaService.searchEntityById()");
        return reservaRepository.findById(id)
                .filter(entity -> entity.getEstadoBD() != EstadoBD.ELIMINADO)
                .orElseThrow(
                        () -> {
                            if (reservaRepository.findById(id).isPresent()){
                                throw new EServiceLayer("La reserva no existe");
                            } else {
                                return new EServiceLayer("La reserva no se encontró");
                            }
                        }
                );
    }
}
