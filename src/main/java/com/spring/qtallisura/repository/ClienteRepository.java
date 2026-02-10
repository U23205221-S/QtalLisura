package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByDNI(String DNI);
    Optional<Cliente> findByDNI(String DNI);
}
