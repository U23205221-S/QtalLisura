package com.spring.qtallisura.service;

import com.spring.qtallisura.dto.request.ProductoRequestDTO;
import com.spring.qtallisura.dto.response.ProductoResponseDTO;
import com.spring.qtallisura.exception.EServiceLayer;
import com.spring.qtallisura.mapper.mapperImpl.ProductoMapper;
import com.spring.qtallisura.model.Categoria;
import com.spring.qtallisura.model.EstadoBD;
import com.spring.qtallisura.model.Producto;
import com.spring.qtallisura.repository.CategoriaRepository;
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
public class ProductoService implements ServiceAbs<ProductoRequestDTO, ProductoResponseDTO> {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    @Override
    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        log.info("ProductoService.create()");
        if (productoRepository.existsByNombre(dto.getNombre())){
            log.warn("ProductoService.create() - El nombre del producto {} ya está registrado",
                    dto.getNombre());
            throw new EServiceLayer("El nombre del producto ya está registrado en el sistema");
        }
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new EServiceLayer("La categoría no existe"));
        Producto model = productoMapper.toModel(dto);
        model.setIdCategoria(categoria);
        model.setEstadoBD(EstadoBD.ACTIVO);
        model = productoRepository.save(model);
        return productoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public List<ProductoResponseDTO> allList() {
        log.info("ProductoService.allList()");
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getEstadoBD() != EstadoBD.ELIMINADO)
                .map(productoMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public ProductoResponseDTO readById(Integer id) {
        log.info("ProductoService.readById()");
        Producto model = searchEntityById(id);
        return productoMapper.toDTO(model);
    }

    @Transactional
    @Override
    public void remove(Integer id) {
        log.info("ProductoService.remove()");
        Producto model = searchEntityById(id);
        model.setEstadoBD(EstadoBD.ELIMINADO);
        productoRepository.save(model);
    }

    @Transactional
    @Override
    public ProductoResponseDTO updateById(Integer id, ProductoRequestDTO dto) {
        log.info("ProductoService.updateById()");
        Producto model_existente = searchEntityById(id);

        // Actualizar solo los campos que vienen en el DTO
        if (dto.getNombre() != null) {
            if (!model_existente.getNombre().equals(dto.getNombre())
                    && productoRepository.existsByNombre(dto.getNombre())){
                log.warn("ProductoService.updateById() - El nombre del producto {} ya está registrado",
                        dto.getNombre());
                throw new EServiceLayer("El nombre del producto ya está registrado en el sistema");
            }
            model_existente.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            model_existente.setDescripcion(dto.getDescripcion());
        }

        if (dto.getIdCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new EServiceLayer("La categoría no existe"));
            model_existente.setIdCategoria(categoria);
        }

        if (dto.getPrecioVenta() != null) {
            model_existente.setPrecioVenta(dto.getPrecioVenta());
        }

        if (dto.getCostoUnitario() != null) {
            model_existente.setCostoUnitario(dto.getCostoUnitario());
        }

        if (dto.getStockActual() != null) {
            model_existente.setStockActual(dto.getStockActual());
        }

        if (dto.getStockMinimo() != null) {
            model_existente.setStockMinimo(dto.getStockMinimo());
        }

        if (dto.getImagenUrl() != null) {
            model_existente.setImagenUrl(dto.getImagenUrl());
        }

        if (dto.getEstadoBD() != null) {
            model_existente.setEstadoBD(dto.getEstadoBD());
        }

        Producto model_actualizado = productoRepository.save(model_existente);
        return productoMapper.toDTO(model_actualizado);
    }

    private Producto searchEntityById(Integer id){
        log.info("ProductoService.searchEntityById()");
        return productoRepository.findById(id)
                .orElseThrow(() -> new EServiceLayer("El producto no existe"));
    }

}
