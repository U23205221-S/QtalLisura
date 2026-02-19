package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByIdPedido_IdPedido(Integer idPedido);
}
