package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.UsuarioRequestDTO;
import com.spring.qtallisura.dto.response.UsuarioResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.UsuarioMapper;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Perfil;
import com.spring.qtallisura.model.Usuario;
import com.spring.qtallisura.repository.PerfilRepository;
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
public class UsuarioService implements ServiceAbs<UsuarioRequestDTO, UsuarioResponseDTO> {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PerfilRepository perfilRepository;

    @Transactional
    @Override
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        log.info("UsuarioService.create()");
        if (usuarioRepository.existsByDNI(dto.getDNI())) {
            log.warn("UsuarioService.create() - El DNI {} ya está registrado",
                    dto.getDNI());
            throw new EServiceLayer("El DNI ya está registrado en el sistema");
        }
        Perfil perfil_reading =
                perfilRepository.findById(dto.getIdPerfil())
                        .orElseThrow(() -> new RuntimeException("El perfil no existe"));
        Usuario model = usuarioMapper.toModel(dto);
        model.setIdPerfil(perfil_reading);
        model.setEstadoBD(EstadoBD.ACTIVO);
        model = usuarioRepository.save(model);
        return usuarioMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<UsuarioResponseDTO> allList() {
        log.info("UsuarioService.allList()");
        return usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(usuarioMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public UsuarioResponseDTO readById(Integer id) {
        log.info("UsuarioService.readById()");
        Usuario usuario = searchEntityById(id);
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("UsuarioService.remove()");
        Usuario usuario = searchEntityById(id);
        usuario.setEstadoBD(EstadoBD.ELIMINADO);
        usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public UsuarioResponseDTO updateById(Integer id, UsuarioRequestDTO dto) {
        log.info("UsuarioService.updateById()");
        Usuario usuario_existente = searchEntityById(id);

        // Actualizar solo los campos que vienen en el DTO
        if (dto.getNombresUsuario() != null) {
            usuario_existente.setNombres(dto.getNombresUsuario());
        }

        if (dto.getApellidosUsuario() != null) {
            usuario_existente.setApellidos(dto.getApellidosUsuario());
        }

        if (dto.getDNI() != null) {
            if (!usuario_existente.getDNI().equals(dto.getDNI())
                    && usuarioRepository.existsByDNI(dto.getDNI())) {
                throw new EServiceLayer("El DNI ya está registrado en el sistema");
            }
            usuario_existente.setDNI(dto.getDNI());
        }

        if (dto.getUsername() != null) {
            usuario_existente.setUsername(dto.getUsername());
        }

        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario_existente.setContrasena(dto.getContrasena());
        }

        if (dto.getIdPerfil() != null) {
            Perfil perfil_reading = perfilRepository.findById(dto.getIdPerfil())
                    .orElseThrow(() -> new RuntimeException("El perfil no existe"));
            usuario_existente.setIdPerfil(perfil_reading);
        }

        if (dto.getImagenUrl() != null) {
            usuario_existente.setImagenUrl(dto.getImagenUrl());
        }

        if (dto.getEstadoBD() != null) {
            usuario_existente.setEstadoBD(dto.getEstadoBD());
        }

        Usuario usuario_save = usuarioRepository.save(usuario_existente);
        return usuarioMapper.toDTO(usuario_save);
    }

    private Usuario searchEntityById(Integer id) {
        log.info("UsuarioService.searchEntityById()");
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("El usuario no existe"));
    }
}
