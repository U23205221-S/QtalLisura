package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.PerfilRequestDTO;
import com.spring.qtallisura.dto.response.PerfilResponseDTO;
import com.spring.qtallisura.mapper.mapperImpl.PerfilMapper;
import com.spring.qtallisura.model.Perfil;
import com.spring.qtallisura.repository.PerfilRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PerfilService implements ServiceAbs<PerfilRequestDTO, PerfilResponseDTO> {

    private final PerfilRepository perfilRepository;
    private final PerfilMapper perfilMapper;

    @Transactional
    @Override
    public PerfilResponseDTO create(PerfilRequestDTO dto) {
        log.info("PerfilService.create()");
        Perfil model = perfilMapper.toModel(dto);
        model = perfilRepository.save(model);
        return perfilMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<PerfilResponseDTO> allList() {
        log.info("PerfilService.allList()");
        return perfilRepository.findAll().stream()
                .map(perfilMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public PerfilResponseDTO readById(Integer id) {
        log.info("PerfilService.readById()");
        Perfil model = searchEntityById(id);
        return perfilMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("PerfilService.remove()");
        Perfil model = searchEntityById(id);
        perfilRepository.delete(model);
    }

    @Transactional
    @Override
    public PerfilResponseDTO updateById(Integer id, PerfilRequestDTO dto) {
        log.info("PerfilService.updateById()");
        Perfil model = searchEntityById(id);
        return perfilMapper.toDTO(model);
    }

    private Perfil searchEntityById(Integer id) {
        log.info("PerfilService.searchEntityById()");
        return perfilRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("PerfilService.searchEntityById() - El perfil con ID {} no fue encontrado", id);
                    return new RuntimeException("El perfil no fue encontrado");
                });
    }
}
