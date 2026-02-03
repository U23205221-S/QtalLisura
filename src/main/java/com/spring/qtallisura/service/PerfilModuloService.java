package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.PerfilModuloRequestDTO;
import com.spring.qtallisura.dto.response.PerfilModuloResponseDTO;
import com.spring.qtallisura.mapper.mapperImpl.PerfilModuloMapper;
import com.spring.qtallisura.model.PerfilModulo;
import com.spring.qtallisura.repository.ModuloRepository;
import com.spring.qtallisura.repository.PerfilModuloRepository;
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
public class PerfilModuloService implements ServiceAbs<PerfilModuloRequestDTO, PerfilModuloResponseDTO> {

    private final PerfilModuloRepository perfilModuloRepository;
    private final PerfilModuloMapper perfilModuloMapper;
    private final ModuloRepository moduloRepository;
    private final PerfilRepository perfilRepository;

    @Transactional
    @Override
    public PerfilModuloResponseDTO create(PerfilModuloRequestDTO dto) {
        log.info("PerfilModuloService.create()");
        PerfilModulo model = perfilModuloMapper.toModel(dto);
        model = perfilModuloRepository.save(model);
        return perfilModuloMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<PerfilModuloResponseDTO> allList() {
        log.info("PerfilModuloService.allList()");
        return perfilModuloRepository.findAll().stream()
                .map(perfilModuloMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public PerfilModuloResponseDTO readById(Integer id) {
        log.info("PerfilModuloService.readById()");
        PerfilModulo model = searchEntityById(id);
        return perfilModuloMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("PerfilModuloService.remove()");
        PerfilModulo model = searchEntityById(id);
        perfilModuloRepository.delete(model);
    }

    @Transactional
    @Override
    public PerfilModuloResponseDTO updateById(Integer id, PerfilModuloRequestDTO dto) {
        log.info("PerfilModuloService.updateById()");
        PerfilModulo model = searchEntityById(id);
        return perfilModuloMapper.toDTO(model);
    }

    private PerfilModulo searchEntityById(Integer id) {
        log.info("PerfilModuloService.searchEntityById()");
        return perfilModuloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La relación Perfil-Modulo no existe"));
    }
}
