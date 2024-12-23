package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    Optional<Estudante> findByEmail(String email);
}
