package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublishProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaPostResponseDTO;
import com.sistema_escolar.dtos.response.ProvaPutResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoPostResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoPutResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final QuestaoRepository questaoRepository;
    private final MailService mailService;

    @Transactional
    public ProvaPostResponseDTO createProva(ProvaPostRequestDTO provaPostRequestDTO, Usuario usuario) {
        Professor professor = professorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não está cadastrado"));
        if (turmaRepository.findByProfessorId(professor.getId()).isEmpty()){
            throw new RuntimeException("Professor deve estar vinculado a uma turma para criar uma prova");
        }
        Prova prova = Prova.builder().disciplina(professor.getDisciplina()).valorTotal(provaPostRequestDTO.getValorTotal()).build();
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPostRequestDTO.getQuestoes().size(); i++) {
            questoes.add(Questao.builder().alternativas(provaPostRequestDTO.getQuestoes().get(i).getAlternativas())
                    .valor(provaPostRequestDTO.getQuestoes().get(i).getValor()).tipoQuestao(provaPostRequestDTO.getQuestoes().get(i).getTipoQuestao())
                    .pergunta(provaPostRequestDTO.getQuestoes().get(i).getPergunta()).criadoPor(provaPostRequestDTO.getQuestoes().get(i).getCriadoPor())
                    .build());
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
        List<QuestaoPostResponseDTO> questaoPostResponseDTOS = new ArrayList<>();
        for (Questao questao : savedQuestoes) {
            questaoPostResponseDTOS.add(QuestaoPostResponseDTO.builder().tipoQuestao(questao.getTipoQuestao())
                    .valor(questao.getValor()).alternativas(questao.getAlternativas())
                    .pergunta(questao.getPergunta()).criadoPor(questao.getCriadoPor())
                    .atualizadoPor(questao.getAtualizadoPor()).build());
        }
        return ProvaPostResponseDTO.builder().valorTotal(savedProva.getValorTotal())
                .questoes(questaoPostResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
    }

    @Transactional
    public ProvaPutResponseDTO updateProva(Long id, ProvaPutRequestDTO provaPutRequestDTO, Usuario usuario) {
        Prova prova = provaRepository.findByIdAndEmailProfessor(id, usuario.getEmail())
                .orElseThrow(() -> new RuntimeException("Prova não pertence a esse professor ou id da prova não existe"));
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPutRequestDTO.getQuestoes().size(); i++) {
            Questao questao = questaoRepository.findById(provaPutRequestDTO.getQuestoes().get(i).getQuestaoId())
                    .orElseThrow(() -> new RuntimeException("Id da questão não existe"));
            questoes.add(questao);
            questoes.get(i).setValor(provaPutRequestDTO.getQuestoes().get(i).getValor());
            questoes.get(i).setAlternativas(provaPutRequestDTO.getQuestoes().get(i).getAlternativas());
            questoes.get(i).setPergunta(provaPutRequestDTO.getQuestoes().get(i).getPergunta());
            questoes.get(i).setTipoQuestao(provaPutRequestDTO.getQuestoes().get(i).getTipoQuestao());
            questoes.get(i).setAtualizadoPor(usuario.getEmail());
        }
        prova.setValorTotal(provaPutRequestDTO.getValorTotal());
        prova.setQuestoes(questoes);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoPutResponseDTO> questaoPutResponseDTOS = new ArrayList<>();
        for (Questao questao : savedQuestoes) {
            questaoPutResponseDTOS.add(QuestaoPutResponseDTO.builder().tipoQuestao(questao.getTipoQuestao())
                    .valor(questao.getValor()).alternativas(questao.getAlternativas())
                    .pergunta(questao.getPergunta()).questaoId(questao.getId()).criadoPor(questao.getCriadoPor())
                    .atualizadoPor(questao.getAtualizadoPor()).build());
        }
        return ProvaPutResponseDTO.builder().provaId(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoPutResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
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
