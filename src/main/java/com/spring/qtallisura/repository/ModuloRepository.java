package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {
    boolean existsByNombre(String nombre);
    Optional<Modulo> findByNombre(String nombre);
}
