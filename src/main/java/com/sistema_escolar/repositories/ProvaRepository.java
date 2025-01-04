package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Nota;
import com.sistema_escolar.entities.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    Optional<Prova> findByIdAndEmailProfessor(Long id, String emailProfessor);
    List<Prova> findByDisciplinaIdAndEmailProfessorAndPublicadoTrue(Long disciplinaId, String emailProfessor);

    @Query("SELECT p FROM Prova p JOIN p.notas n WHERE n IN :notas")
    List<Prova> findByNotas(@Param("notas") List<Nota> notas);
    boolean existsByIdAndPublicadoTrue(Long id);
}
