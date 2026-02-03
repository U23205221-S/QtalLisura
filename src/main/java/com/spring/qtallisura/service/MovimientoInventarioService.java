package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.MovimientoInventarioRequestDTO;
import com.spring.qtallisura.dto.response.MovimientoInventarioResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.MovimientoInventarioMapper;
import com.spring.qtallisura.model.MovimientoInventario;
import com.spring.qtallisura.model.Producto;
import com.spring.qtallisura.repository.MovimientoInventarioRepository;
import com.spring.qtallisura.repository.ProductoRepository;
import com.spring.qtallisura.service.abstractService.ServiceAbs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovimientoInventarioService implements ServiceAbs<MovimientoInventarioRequestDTO, MovimientoInventarioResponseDTO> {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final MovimientoInventarioMapper movimientoInventarioMapper;
    private final ProductoRepository productoRepository;

    @Transactional
    @Override
    public MovimientoInventarioResponseDTO create(MovimientoInventarioRequestDTO dto) {
        log.info("MovimientoInventarioService.create()");

        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new EServiceLayer("El producto no existe"));

        MovimientoInventario model = movimientoInventarioMapper.toModel(dto);
        model.setIdProducto(producto);

        model = movimientoInventarioRepository.save(model);
        return movimientoInventarioMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<MovimientoInventarioResponseDTO> allList() {
        log.info("MovimientoInventarioService.allList()");
        return movimientoInventarioRepository.findAll().stream()
                .map(movimientoInventarioMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public MovimientoInventarioResponseDTO readById(Integer id) {
        log.info("MovimientoInventarioService.readById()");
        MovimientoInventario model = searchEntityById(id);
        return movimientoInventarioMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("MovimientoInventarioService.remove()");
        MovimientoInventario model = searchEntityById(id);
        movimientoInventarioRepository.delete(model);
    }

    @Transactional
    @Override
    public MovimientoInventarioResponseDTO updateById(Integer id, MovimientoInventarioRequestDTO dto) {
        log.info("MovimientoInventarioService.updateById()");
        MovimientoInventario model_existente = searchEntityById(id);

        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new EServiceLayer("El producto no existe"));
            model_existente.setIdProducto(producto);
        }

        if (dto.getTipoMovimiento() != null) {
            model_existente.setTipoMovimiento(dto.getTipoMovimiento());
        }

        if (dto.getCantidad() != null) {
            model_existente.setCantidad(dto.getCantidad());
        }

        if (dto.getFechaMovimiento() != null) {
            model_existente.setFechaMovimiento(dto.getFechaMovimiento());
        }

        MovimientoInventario model_actualizado = movimientoInventarioRepository.save(model_existente);
        return movimientoInventarioMapper.toDTO(model_actualizado);
    }

    private MovimientoInventario searchEntityById(Integer id){
        log.info("MovimientoInventarioService.searchEntityById()");
        return movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("El movimiento de inventario no existe"));
    }
}
