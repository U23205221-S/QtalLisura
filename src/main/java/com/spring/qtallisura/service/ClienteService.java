package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ClienteRequestDTO;
import com.spring.qtallisura.dto.response.ClienteResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.ClienteMapper;
import com.spring.qtallisura.model.Cliente;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.repository.ClienteRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteService implements ServiceAbs<ClienteRequestDTO, ClienteResponseDTO> {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        log.info("ClienteService.create()");
        if (clienteRepository.existsByDNI(dto.getDNI())){
            log.warn("ClienteService.create() - El DNI {} ya está registrado",
                    dto.getDNI());
            throw new EServiceLayer("El DNI ya está registrado en el sistema");
        }
        Cliente model = clienteMapper.toModel(dto);

        // Encriptar contraseña antes de guardar
        model.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        model.setEstadoBD(EstadoBD.ACTIVO);

        model = clienteRepository.save(model);
        return clienteMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<ClienteResponseDTO> allList() {
        log.info("ClienteService.allList()");
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(clienteMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public ClienteResponseDTO readById(Integer id) {
        log.info("ClienteService.readById()");
        Cliente model = searchEntityById(id);
        return clienteMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("ClienteService.remove()");
        Cliente model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        clienteRepository.save(model);
    }

    @Transactional
    @Override
    public ClienteResponseDTO updateById(Integer id, ClienteRequestDTO dto) {
        log.info("ClienteService.updateById()");
        Cliente model_existente = searchEntityById(id);

        if (!model_existente.getDNI().equals(dto.getDNI())
                && clienteRepository.existsByDNI(dto.getDNI())){
            log.warn("ClienteService.updateById() - El DNI {} ya está registrado",
                    dto.getDNI());
            throw new EServiceLayer("El DNI ya está registrado en el sistema");
        }

        Cliente model_actualizado = clienteMapper.toModel(dto);
        model_actualizado.setIdCliente(model_existente.getIdCliente());
        model_actualizado.setEstadoBD(model_existente.getEstadoBD());

        model_actualizado = clienteRepository.save(model_actualizado);
        return clienteMapper.toDTO(model_actualizado);
    }

    private Cliente searchEntityById(Integer id){
        log.info("ClienteService.searchEntityById()");
        return clienteRepository.findById(id)
                .filter(entity -> entity.getEstadoBD() != EstadoBD.ELIMINADO)
                .orElseThrow(
                        () -> {
                            if (clienteRepository.findById(id).isPresent()){
                                throw new EServiceLayer("El cliente no existe");
                            } else {
                                return new EServiceLayer("El cliente no se encontró");
                            }
                        }
                );
    }
}
