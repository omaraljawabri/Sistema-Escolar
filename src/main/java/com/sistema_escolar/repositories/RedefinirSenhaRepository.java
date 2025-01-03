package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.RedefinirSenha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RedefinirSenhaRepository extends JpaRepository<RedefinirSenha, Long> {
    Optional<RedefinirSenha> findByCodigoDeVerificacao(String codigoDeVerificacao);
}
