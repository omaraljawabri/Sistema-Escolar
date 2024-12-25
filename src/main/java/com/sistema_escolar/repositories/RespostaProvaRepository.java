package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.RespostaProva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespostaProvaRepository extends JpaRepository<RespostaProva, Long> {
    List<RespostaProva> findByEstudanteIdAndProvaId(Long estudanteId, Long provaId);
}
