package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCodigoDeVerificacao(String codigo);
}
