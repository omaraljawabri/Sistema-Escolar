package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
}
