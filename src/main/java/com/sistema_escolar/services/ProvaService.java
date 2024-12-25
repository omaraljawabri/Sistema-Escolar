package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublishProvaRequestDTO;
import com.sistema_escolar.dtos.response.*;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.utils.mappers.ProvaMapper;
import com.sistema_escolar.utils.mappers.QuestaoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final QuestaoRepository questaoRepository;
    private final MailService mailService;

    @Transactional
    public ProvaResponseDTO createProva(ProvaPostRequestDTO provaPostRequestDTO, Usuario usuario) {
        Professor professor = professorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não está cadastrado"));
        if (turmaRepository.findByProfessorId(professor.getId()).isEmpty()){
            throw new RuntimeException("Professor deve estar vinculado a uma turma para criar uma prova");
        }
        Prova prova = Prova.builder().disciplina(professor.getDisciplina()).valorTotal(provaPostRequestDTO.getValorTotal()).build();
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPostRequestDTO.getQuestoes().size(); i++) {
            questoes.add(QuestaoMapper.INSTANCE.toQuestao(provaPostRequestDTO.getQuestoes().get(i)));
            if (questoes.get(i).getProvas() != null){
                questoes.get(i).getProvas().add(prova);
            } else{
                questoes.get(i).setProvas(List.of(prova));
            }
            if (questoes.get(i).getCriadoPor() == null){
                questoes.get(i).setCriadoPor(professor.getEmail());
            }
        }
        prova.setQuestoes(questoes);
        prova.setDisciplina(professor.getDisciplina());
        prova.setEmailProfessor(professor.getEmail());
        prova.setIsPublished(false);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoResponseDTO> questaoResponseDTOS
                = savedQuestoes.stream().map(QuestaoMapper.INSTANCE::toQuestaoResponseDTO).toList();
        return ProvaResponseDTO.builder().id(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
    }

    @Transactional
    public ProvaResponseDTO updateProva(Long id, ProvaPutRequestDTO provaPutRequestDTO, Usuario usuario) {
        Prova prova = provaRepository.findByIdAndEmailProfessor(id, usuario.getEmail())
                .orElseThrow(() -> new RuntimeException("Prova não pertence a esse professor ou id da prova não existe"));
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPutRequestDTO.getQuestoes().size(); i++) {
            Questao questao = questaoRepository.findById(provaPutRequestDTO.getQuestoes().get(i).getId())
                    .orElseThrow(() -> new RuntimeException("Id da questão não existe"));
            questoes.add(questao);
            questoes.get(i).setValor(provaPutRequestDTO.getQuestoes().get(i).getValor());
            questoes.get(i).setAlternativas(provaPutRequestDTO.getQuestoes().get(i).getAlternativas());
            questoes.get(i).setPergunta(provaPutRequestDTO.getQuestoes().get(i).getPergunta());
            questoes.get(i).setTipoQuestao(provaPutRequestDTO.getQuestoes().get(i).getTipoQuestao());
            questoes.get(i).setAtualizadoPor(usuario.getEmail());
            questoes.get(i).setRespostaCorreta(provaPutRequestDTO.getQuestoes().get(i).getRespostaCorreta());
        }
        prova.setValorTotal(provaPutRequestDTO.getValorTotal());
        prova.setQuestoes(questoes);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoResponseDTO> questaoResponseDTOS = savedQuestoes.stream().map(QuestaoMapper.INSTANCE::toQuestaoResponseDTO).toList();
        return ProvaResponseDTO.builder().id(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
    }

    public void publishProva(PublishProvaRequestDTO publishProvaRequestDTO, Long id, Usuario usuario) {
        Professor professor = professorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não foi cadastrado"));
        Prova prova = provaRepository.findByIdAndEmailProfessor(id, professor.getEmail())
                .orElseThrow(() -> new RuntimeException("Prova não pertence a esse professor ou id da prova não existe"));
        prova.setIsPublished(true);
        prova.setExpirationTime(LocalDateTime.now().plusHours(publishProvaRequestDTO.getExpirationHours()).plusMinutes(publishProvaRequestDTO.getExpirationMinutes()));
        provaRepository.save(prova);
        Turma turma = turmaRepository.findByProfessorId(professor.getId())
                .orElseThrow(() -> new RuntimeException("Professor não está vinculado a uma turma"));
        String turmaName = turma.getName();
        String disciplinaName = turma.getDisciplina().getName();
        for (Estudante estudante : turma.getEstudantes()) {
            String mensagem = "Olá, " + estudante.getFirstName() + ", uma nova prova foi postada na turma " +
                    turmaName + " da disciplina " + disciplinaName + "!";
            mailService.sendEmail(estudante.getEmail(), "Postagem de prova", mensagem);
        }
    }
}
