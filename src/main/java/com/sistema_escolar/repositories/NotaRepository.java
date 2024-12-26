package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Long> {
}
