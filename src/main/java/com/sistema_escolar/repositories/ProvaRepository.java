package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    Optional<Prova> findByIdAndEmailProfessor(Long id, String emailProfessor);
}
