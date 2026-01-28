package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.PerfilModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilModuloRepository extends JpaRepository<PerfilModulo, Integer> {
}
