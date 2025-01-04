package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.RespostaProva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespostaProvaRepository extends JpaRepository<RespostaProva, Long> {
    List<RespostaProva> findByEstudanteIdAndProvaId(Long estudanteId, Long provaId);
    List<RespostaProva> findAllByProvaIdAndRespondidaTrue(Long id);
    Optional<RespostaProva> findByQuestaoIdAndProvaIdAndEstudanteId(Long questaoId, Long provaId, Long estudanteId);
    List<RespostaProva> findByEstudanteIdAndProvaIdAndAvaliadaTrue(Long estudanteId, Long provaId);
    boolean existsByProvaIdAndEstudanteId(Long provaId, Long estudanteId);
}
