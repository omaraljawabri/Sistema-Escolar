package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Questao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    Page<Questao> findAll(Pageable pageable);
    boolean existsByIdAndProvasId(Long id, Long provasId);
}
