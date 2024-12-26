package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.CreateDisciplinaRequestDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.repositories.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public void createDisciplina(CreateDisciplinaRequestDTO createDisciplinaRequestDTO){
        Optional<Disciplina> disciplinaOptional = disciplinaRepository.findByName(createDisciplinaRequestDTO.getName());
        if (disciplinaOptional.isPresent()){
            throw new EntityAlreadyExistsException("Nome da disciplina já existe");
        }
        Disciplina disciplinaToSave = Disciplina.builder().name(createDisciplinaRequestDTO.getName()).build();
        disciplinaRepository.save(disciplinaToSave);
    }
}
