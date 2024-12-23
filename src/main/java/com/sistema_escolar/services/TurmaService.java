package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;

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

    public void addEstudante(AddTurmaRequestDTO addTurmaRequestDTO){
        Estudante estudante = estudanteRepository.findByEmail(addTurmaRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email do estudante que deseja adicionar não existe"));
        Turma turma = turmaRepository.findById(addTurmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma selecionada não existe"));
        if (turmaRepository.findByEstudantes(estudante).isPresent()){
            throw new RuntimeException("Estudante já está cadastrado nesta turma!");
        }
        turma.getEstudantes().add(estudante);
        estudante.getTurmas().add(turma);
        turmaRepository.save(turma);
    }

    @Transactional
    public void addProfessor(AddTurmaRequestDTO addTurmaRequestDTO){
        Professor professor = professorRepository.findByEmail(addTurmaRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email do professor que deseja adicionar não existe"));
        Turma turma = turmaRepository.findById(addTurmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma selecionada não existe"));
        if (turmaRepository.findByIdAndProfessor(turma.getId(), professor).isPresent()){
            throw new RuntimeException("Professor já está cadastrado nesta turma!");
        }
        professor.setDisciplina(turma.getDisciplina());
        turma.setProfessor(professor);
        professorRepository.save(professor);
        turmaRepository.save(turma);
    }

    public CodeResponseDTO generateCode(TurmaRequestDTO turmaRequestDTO) {
        String generatedCode = CodeGenerator.generateCode();
        Turma turma = turmaRepository.findById(turmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma selecionada não existe"));
        turma.setTurmaCode(generatedCode);
        turma.setCodeExpirationTime(LocalDateTime.now().plusDays(7));
        turmaRepository.save(turma);
        return CodeResponseDTO.builder().code(generatedCode).build();
    }

    public CodeResponseDTO generateCode(Usuario usuario){
        Turma turma = turmaRepository.findByProfessorId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Professor não esta vinculado a nenhuma turma"));
        String generatedCode = CodeGenerator.generateCode();
        turma.setTurmaCode(generatedCode);
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(7));
        turmaRepository.save(turma);
        return CodeResponseDTO.builder().code(generatedCode).build();
    }
}
