package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.CriarDisciplinaRequestDTO;
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

    public void criarDisciplina(CriarDisciplinaRequestDTO criarDisciplinaRequestDTO){
        Optional<Disciplina> disciplinaOptional = disciplinaRepository.findByNome(criarDisciplinaRequestDTO.getNome());
        if (disciplinaOptional.isPresent()){
            throw new EntityAlreadyExistsException("Nome da disciplina já existe");
        }
        Disciplina disciplinaToSave = Disciplina.builder().nome(criarDisciplinaRequestDTO.getNome()).build();
        disciplinaRepository.save(disciplinaToSave);
    }
}
