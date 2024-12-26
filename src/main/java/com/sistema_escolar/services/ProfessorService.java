package com.sistema_escolar.services;

import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.repositories.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public Professor buscarPorId(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não foi encontrado"));
    }
}
