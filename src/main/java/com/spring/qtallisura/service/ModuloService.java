package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ModuloRequestDTO;
import com.spring.qtallisura.dto.response.ModuloResponseDTO;
import com.spring.qtallisura.mapper.mapperImpl.ModuloMapper;
import com.spring.qtallisura.model.Modulo;
import com.spring.qtallisura.repository.ModuloRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModuloService implements ServiceAbs<ModuloRequestDTO, ModuloResponseDTO> {

    private final ModuloRepository moduloRepository;
    private final ModuloMapper moduloMapper;

    @Transactional
    @Override
    public ModuloResponseDTO create(ModuloRequestDTO dto) {
        log.info("ModuloService.create()");
        if (moduloRepository.existsByNombre(dto.getNombre())) {
            log.warn("ModuloService.create() - El módulo {} ya está registrado",
                    dto.getNombre());
            throw new RuntimeException("El módulo ya está registrado en el sistema");
        }
        Modulo model = moduloMapper.toModel(dto);
        model = moduloRepository.save(model);
        return moduloMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<ModuloResponseDTO> allList() {
        log.info("ModuloService.allList()");
        return moduloRepository.findAll().stream()
                .map(moduloMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public ModuloResponseDTO readById(Integer id) {
        log.info("ModuloService.readById()");
        Modulo model = searchEntityById(id);
        return moduloMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("ModuloService.remove()");
        Modulo model = searchEntityById(id);
        moduloRepository.delete(model);
    }

    @Transactional
    @Override
    public ModuloResponseDTO updateById(Integer id, ModuloRequestDTO dto) {
        log.info("ModuloService.updateById()");
        Modulo model = searchEntityById(id);
        model = moduloRepository.save(model);
        return moduloMapper.toDTO(model);
    }

    private Modulo searchEntityById(Integer id) {
        log.info("ModuloService.searchEntityById()");
        return moduloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("ModuloService.searchEntityById() - El módulo con ID {} no existe", id);
                    return new RuntimeException("El módulo no existe");
                });
    }

}
