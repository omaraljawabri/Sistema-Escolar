package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Turma;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;

    public void createTurma(CreateTurmaRequestDTO createTurmaRequestDTO){
        Disciplina disciplina
                = disciplinaRepository.findById(createTurmaRequestDTO.getDisciplinaId()).orElseThrow(() -> new RuntimeException("Disciplina passada não existe"));
        Optional<Turma> turmaOptional = turmaRepository.findByNameAndDisciplina(createTurmaRequestDTO.getName(), disciplina);
        if (turmaOptional.isPresent()){
            throw new RuntimeException("A turma que está sendo criada já existe");
        }
        Turma turmaToSave = Turma.builder().name(createTurmaRequestDTO.getName()).disciplina(disciplina).build();
        turmaRepository.save(turmaToSave);
    }

}
