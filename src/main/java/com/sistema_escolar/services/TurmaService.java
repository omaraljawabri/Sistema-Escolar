package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.AddEstudanteTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Turma;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EstudanteRepository estudanteRepository;

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

    public void addEstudante(AddEstudanteTurmaRequestDTO addEstudanteTurmaRequestDTO){
        Estudante estudante = estudanteRepository.findByEmail(addEstudanteTurmaRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email do estudante que deseja adicionar não existe"));
        Turma turma = turmaRepository.findById(addEstudanteTurmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma selecionada não existe"));
        if (turmaRepository.findByEstudantes(estudante).isPresent()){
            throw new RuntimeException("Estudante já está cadastrado nesta turma!");
        }
        turma.getEstudantes().add(estudante);
        estudante.getTurmas().add(turma);
        turmaRepository.save(turma);
    }
}
