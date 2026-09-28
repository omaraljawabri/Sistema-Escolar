package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CodeRequestDTO;
import com.sistema_escolar.dtos.request.CriarTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.exceptions.*;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.utils.CodeGenerator;
import com.sistema_escolar.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;

    public void criarTurma(CriarTurmaRequestDTO criarTurmaRequestDTO){
        Disciplina disciplina
                = disciplinaRepository.findById(criarTurmaRequestDTO.getDisciplinaId())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina passada não existe"));
        Optional<Turma> turmaOptional = turmaRepository.findByNomeAndDisciplina(criarTurmaRequestDTO.getNome(), disciplina);
        if (turmaOptional.isPresent()){
            throw new EntityAlreadyExistsException("A turma que está sendo criada já existe");
        }
        Turma turmaToSave = Turma.builder().nome(criarTurmaRequestDTO.getNome()).disciplina(disciplina).build();
        turmaRepository.save(turmaToSave);
    }

    public void addEstudante(AddTurmaRequestDTO addTurmaRequestDTO){
        Estudante estudante = estudanteRepository.findByEmail(addTurmaRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email do estudante que deseja adicionar não existe"));
        Turma turma = turmaRepository.findById(addTurmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma selecionada não existe"));
        if (turmaRepository.findByEstudantes(estudante).isPresent()){
            throw new UserAlreadyBelongsToAnEntityException("Estudante já está cadastrado nesta turma!");
        }
        if (turma.getEstudantes() != null){
            List<Estudante> estudantes = new ArrayList<>(turma.getEstudantes());
            estudantes.add(estudante);
            turma.setEstudantes(estudantes);
        } else{
            turma.setEstudantes(List.of(estudante));
        }
        if (estudante.getTurmas() != null){
            List<Turma> turmas = new ArrayList<>(estudante.getTurmas());
            turmas.add(turma);
            estudante.setTurmas(turmas);
        } else{
            estudante.setTurmas(List.of(turma));
        }
        turmaRepository.save(turma);
    }

    @Transactional
    public void addProfessor(AddTurmaRequestDTO addTurmaRequestDTO){
        Professor professor = professorRepository.findByEmail(addTurmaRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email do professor que deseja adicionar não existe"));
        Turma turma = turmaRepository.findById(addTurmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma selecionada não existe"));
        if (turmaRepository.findByIdAndProfessor(turma.getId(), professor).isPresent()){
            throw new UserAlreadyBelongsToAnEntityException("Professor já está cadastrado nesta turma!");
        }
        if (professor.getDisciplina() != null && !professor.getDisciplina().getNome().equals(turma.getDisciplina().getNome())){
            throw new UserAlreadyBelongsToAnEntityException("Professor já está cadastrado em outra disciplina");
        }
        professor.setDisciplina(turma.getDisciplina());
        turma.setProfessor(professor);
        professorRepository.save(professor);
        turmaRepository.save(turma);
    }

    public CodeResponseDTO gerarCodigo(TurmaRequestDTO turmaRequestDTO) {
        String generatedCode = CodeGenerator.gerarCodigo();
        Turma turma = turmaRepository.findById(turmaRequestDTO.getTurmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma selecionada não existe"));
        turma.setCodigoTurma(generatedCode);
        turma.setTempoExpiracaoCodigo(LocalDateTime.now().plusDays(7));
        turmaRepository.save(turma);
        return CodeResponseDTO.builder().codigo(generatedCode).build();
    }

    public CodeResponseDTO gerarCodigo(Usuario usuario){
        Turma turma = turmaRepository.findByProfessorId(usuario.getId())
                .orElseThrow(() -> new UserDoesntBelongException("Professor não esta vinculado a nenhuma turma"));
        String generatedCode = CodeGenerator.gerarCodigo();
        turma.setCodigoTurma(generatedCode);
        turma.setTempoExpiracaoCodigo(LocalDateTime.now().plusHours(7));
        turmaRepository.save(turma);
        return CodeResponseDTO.builder().codigo(generatedCode).build();
    }

    public void entrarTurma(CodeRequestDTO codeRequestDTO, Usuario usuario){
        Turma turma = turmaRepository.findByCodigoTurma(codeRequestDTO.getCodigo())
                .orElseThrow(() -> new InvalidCodeException("Código de turma não existe!"));
        if (turma.getTempoExpiracaoCodigo().isBefore(LocalDateTime.now())){
            throw new InvalidCodeException("Código de turma está expirado!");
        }
        if (usuario.getRole() == UserRole.PROFESSOR ){
            if (turmaRepository.findByProfessorIdAndCodigoTurma(usuario.getId(), codeRequestDTO.getCodigo()).isPresent()) {
                throw new UserAlreadyBelongsToAnEntityException("Professor já está vinculado a essa turma");
            } else{
                turma.setProfessor(professorRepository.findById(usuario.getId()).get());
                turmaRepository.save(turma);
            }
        } else if (usuario.getRole() == UserRole.ESTUDANTE){
            Disciplina disciplina = turma.getDisciplina();
            Estudante estudante = estudanteRepository.findById(usuario.getId()).get();
            if (estudante.getDisciplinas() != null) {
                for (Disciplina disciplinas : estudante.getDisciplinas()) {
                    if (Objects.equals(disciplinas.getId(), disciplina.getId())) {
                        throw new UserAlreadyBelongsToAnEntityException("Estudante já está vinculado a uma turma desta disciplina!");
                    }
                }
            }
            if (turma.getEstudantes() != null){
                List<Estudante> estudantes = new ArrayList<>(turma.getEstudantes());
                estudantes.add(estudante);
                turma.setEstudantes(estudantes);
            } else{
                turma.setEstudantes(List.of(estudante));
            }
            if (estudante.getTurmas() != null){
                List<Turma> turmas = new ArrayList<>(estudante.getTurmas());
                turmas.add(turma);
                estudante.setTurmas(turmas);
            } else{
                estudante.setTurmas(List.of(turma));
            }
            if (disciplina.getEstudantes() != null){
                List<Estudante> estudantes = new ArrayList<>(disciplina.getEstudantes());
                estudantes.add(estudante);
                disciplina.setEstudantes(estudantes);
            } else{
                disciplina.setEstudantes(List.of(estudante));
            }
            if (estudante.getDisciplinas() != null){
                List<Disciplina> disciplinas = new ArrayList<>(estudante.getDisciplinas());
                disciplinas.add(disciplina);
                estudante.setDisciplinas(disciplinas);
            } else{
                estudante.setDisciplinas(List.of(disciplina));
            }
            log.info(estudante.getTurmas().getFirst().getId());
            estudanteRepository.save(estudante);
            turmaRepository.save(turma);
            disciplinaRepository.save(disciplina);
        }
    }
}
