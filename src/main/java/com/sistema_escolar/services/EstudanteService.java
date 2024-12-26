package com.sistema_escolar.services;

import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.EstudanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstudanteService {

    private final EstudanteRepository estudanteRepository;

    public Estudante buscarPorId(Long id){
        return estudanteRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Estudante não encontrado"));
    }
}
