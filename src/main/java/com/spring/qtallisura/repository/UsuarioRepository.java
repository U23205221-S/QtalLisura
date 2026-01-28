package com.spring.qtallisura.repository;

import com.spring.qtallisura.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByDNI(String dni);
    Optional<Usuario> findByUsername(String username);
}
