package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    Optional<Turma> findByNameAndDisciplina(String name, Disciplina disciplina);
}
