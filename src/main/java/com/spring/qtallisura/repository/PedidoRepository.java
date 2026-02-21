package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    boolean existsByCodigo(String codigo);
    Optional<Pedido> findByCodigo(String codigo);
    List<Pedido> findByIdUsuario_IdUsuario(Integer idUsuario);
    List<Pedido> findByIdCliente_IdCliente(Integer idCliente);
}
