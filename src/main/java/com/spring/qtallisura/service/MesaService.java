package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.MesaRequestDTO;
import com.spring.qtallisura.dto.response.MesaResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.MesaMapper;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Mesa;
import com.spring.qtallisura.repository.MesaRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MesaService implements ServiceAbs<MesaRequestDTO, MesaResponseDTO> {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Transactional
    @Override
    public MesaResponseDTO create(MesaRequestDTO dto) {
        log.info("MesaService.create()");
        if (mesaRepository.existsByNumeroMesa(dto.getNumeroMesa())){
            log.warn("MesaService.create() - El número de mesa {} ya está registrado",
                    dto.getNumeroMesa());
            throw new EServiceLayer("El número de mesa ya está registrado en el sistema");
        }
        Mesa model = mesaMapper.toModel(dto);
        model.setEstadoBD(EstadoBD.ACTIVO);
        model = mesaRepository.save(model);
        return mesaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<MesaResponseDTO> allList() {
        log.info("MesaService.allList()");
        return mesaRepository.findAll().stream()
                .filter(mesa -> mesa.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(mesaMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public MesaResponseDTO readById(Integer id) {
        log.info("MesaService.readById()");
        Mesa model = searchEntityById(id);
        return mesaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("MesaService.remove()");
        Mesa model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        mesaRepository.save(model);
    }

    @Transactional
    @Override
    public MesaResponseDTO updateById(Integer id, MesaRequestDTO dto) {
        log.info("MesaService.updateById()");
        Mesa model_existente = searchEntityById(id);

        if (!model_existente.getNumeroMesa().equals(dto.getNumeroMesa())
                && mesaRepository.existsByNumeroMesa(dto.getNumeroMesa())){
            log.warn("MesaService.updateById() - El número de mesa {} ya está registrado",
                    dto.getNumeroMesa());
            throw new EServiceLayer("El número de mesa ya está registrado en el sistema");
        }

        Mesa model_actualizado = mesaMapper.toModel(dto);
        model_actualizado.setIdMesa(model_existente.getIdMesa());
        model_actualizado.setEstadoBD(model_existente.getEstadoBD());

        model_actualizado = mesaRepository.save(model_actualizado);
        return mesaMapper.toDTO(model_actualizado);
    }

    private Mesa searchEntityById(Integer id){
        log.info("MesaService.searchEntityById()");
        return mesaRepository.findById(id)
                .filter(entity -> entity.getEstadoBD() != EstadoBD.ELIMINADO)
                .orElseThrow(
                        () -> {
                            if (mesaRepository.findById(id).isPresent()){
                                throw new EServiceLayer("La mesa no existe");
                            } else {
                                return new EServiceLayer("La mesa no se encontró");
                            }
                        }
                );
    }
}
