package com.sistema_escolar.repositories;

import com.sistema_escolar.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
