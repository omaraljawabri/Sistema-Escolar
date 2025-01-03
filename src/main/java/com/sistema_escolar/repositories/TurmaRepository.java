package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    Optional<Turma> findByNomeAndDisciplina(String name, Disciplina disciplina);
    Optional<Turma> findByEstudantes(Estudante estudante);
    Optional<Turma> findByIdAndProfessor(Long id, Professor professor);
    Optional<Turma> findByProfessorId(Long id);
    Optional<Turma> findByCodigoTurma(String codigo);
    Optional<Turma> findByIdAndProfessorId(Long id, Long professorId);
    Optional<Turma> findByProfessorIdAndCodigoTurma(Long id, String codigo);
    long countByDisciplinaId(Long disciplinaId);
}
