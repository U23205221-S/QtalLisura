package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {
    List<Resena> findByIdCliente_IdCliente(Integer idCliente);
    boolean existsByIdPedido_IdPedidoAndIdCliente_IdClienteAndIdProducto_IdProducto(
            Integer idPedido, Integer idCliente, Integer idProducto);
}
