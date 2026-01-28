package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.CategoriaRequestDTO;
import com.spring.qtallisura.dto.response.CategoriaResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.CategoriaMapper;
import com.spring.qtallisura.model.Categoria;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.repository.CategoriaRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriaService implements ServiceAbs<CategoriaRequestDTO,CategoriaResponseDTO> {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    @Override
    public CategoriaResponseDTO create(CategoriaRequestDTO dto) {
        log.info("CategoriaService.create()");
        if (categoriaRepository.existsByNombre(dto.getNombre())){
            log.warn("CategoriaService.create() - El nombre de la categoría {} ya está registrado",
                    dto.getNombre());
            throw new EServiceLayer("El nombre de la categoría ya está registrado en el sistema");
        }
        Categoria model = categoriaMapper.toModel(dto);
        model.setEstadoBD(EstadoBD.ACTIVO);
        model = categoriaRepository.save(model);
        return categoriaMapper.toDTO(model);

    }

    @Transactional
    @Override
    public List<CategoriaResponseDTO> allList() {
        log.info("CategoriaService.allList()");
        return categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(categoriaMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public CategoriaResponseDTO readById(Integer id) {
        log.info("CategoriaService.readById()");
        Categoria model = searchEntityById(id);
        return categoriaMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("CategoriaService.remove()");
        Categoria model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        categoriaRepository.save(model);
    }

    @Transactional
    @Override
    public CategoriaResponseDTO updateById(Integer id, CategoriaRequestDTO dto) {
        log.info("CategoriaService.updateById()");
        Categoria model_existente = searchEntityById(id);

        if (!model_existente.getNombre().equals(dto.getNombre())
                && categoriaRepository.existsByNombre(dto.getNombre())){
            log.warn("CategoriaService.updateById() - El nombre de la categoría {} ya está registrado",
                    dto.getNombre());
            throw new EServiceLayer("El nombre de la categoría ya está registrado en el sistema");
        }

        Categoria model_actualizado = categoriaMapper.toModel(dto);
        model_actualizado.setIdCategoria(model_existente.getIdCategoria());
        model_actualizado.setEstadoBD(model_existente.getEstadoBD());

        model_actualizado = categoriaRepository.save(model_actualizado);
        return categoriaMapper.toDTO(model_actualizado);
    }

    private Categoria searchEntityById(Integer id){
        log.info("CategoriaService.searchEntityById()");
        return categoriaRepository.findById(id)
                .filter(entity -> entity.getEstadoBD() != EstadoBD.ELIMINADO)
                .orElseThrow(
                        () -> {
                            if (categoriaRepository.findById(id).isPresent()){
                                throw new EServiceLayer("La categoría no existe");
                            } else {
                                return new EServiceLayer("La categoría no se encontró");
                            }
                        }
                );
    }
}
