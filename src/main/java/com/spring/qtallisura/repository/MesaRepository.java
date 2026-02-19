package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    boolean existsByNumeroMesa(Integer numeroMesa);
    List<Mesa> findByEstadoMesa(Mesa.EstadoMesa estadoMesa);
}
