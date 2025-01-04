package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Nota;
import com.sistema_escolar.entities.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    Optional<Prova> findByIdAndEmailProfessor(Long id, String emailProfessor);
    List<Prova> findByDisciplinaIdAndEmailProfessorAndPublicadoTrue(Long disciplinaId, String emailProfessor);
    List<Prova> findByNotas(List<Nota> notas);
    boolean existsByIdAndPublicadoTrue(Long id);
}
