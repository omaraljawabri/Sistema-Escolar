package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    Optional<Nota> findByEstudanteIdAndProvaId(Long estudanteId, Long provaId);
}
