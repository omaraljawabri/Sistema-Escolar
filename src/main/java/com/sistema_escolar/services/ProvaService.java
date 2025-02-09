package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublicarProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.TestErrorException;
import com.sistema_escolar.exceptions.UserDoesntBelongException;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.RespostaProvaRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.utils.mappers.QuestaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final ProfessorService professorService;
    private final TurmaRepository turmaRepository;
    private final QuestaoRepository questaoRepository;
    private final RespostaProvaRepository respostaProvaRepository;
    private final MailService mailService;
    private final EstudanteService estudanteService;

    @Transactional
    public ProvaResponseDTO criarProva(ProvaPostRequestDTO provaPostRequestDTO, Usuario usuario) {
        Professor professor = professorService.buscarPorId(usuario.getId());
        if (turmaRepository.findByProfessorId(professor.getId()).isEmpty()){
            throw new UserDoesntBelongException("Professor deve estar vinculado a uma turma para criar uma prova");
        }
        Prova prova = Prova.builder().disciplina(professor.getDisciplina()).valorTotal(provaPostRequestDTO.getValorTotal()).build();
        List<Questao> questoes = adicionarQuestoes(prova, professor, provaPostRequestDTO);
        prova.setQuestoes(questoes);
        prova.setDisciplina(professor.getDisciplina());
        prova.setEmailProfessor(professor.getEmail());
        prova.setPublicado(false);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoResponseDTO> questaoResponseDTOS
                = savedQuestoes.stream().map(QuestaoMapper.INSTANCE::toQuestaoResponseDTO).toList();
        return ProvaResponseDTO.builder().id(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
    }

    @Transactional
    public ProvaResponseDTO atualizarProva(Long id, ProvaPutRequestDTO provaPutRequestDTO, Usuario usuario) {
        Prova prova = buscarPorIdEEmailDoProfessor(id, usuario.getEmail());
        List<Questao> questoes = adicionarQuestoes(provaPutRequestDTO, usuario);
        prova.setValorTotal(provaPutRequestDTO.getValorTotal());
        prova.setQuestoes(questoes);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoResponseDTO> questaoResponseDTOS = savedQuestoes.stream().map(QuestaoMapper.INSTANCE::toQuestaoResponseDTO).toList();
        return ProvaResponseDTO.builder().id(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoResponseDTOS).emailProfessor(savedProva.getEmailProfessor()).build();
    }

    public void publicarProva(PublicarProvaRequestDTO publicarProvaRequestDTO, Long id, Usuario usuario) {
        if (provaRepository.existsByIdAndPublicadoTrue(id)) {
            throw new TestErrorException("Prova já foi publicada");
        }
        Professor professor = professorService.buscarPorId(usuario.getId());
        Prova prova = buscarPorIdEEmailDoProfessor(id, professor.getEmail());
        prova.setPublicado(true);
        prova.setTempoDeExpiracao(LocalDateTime.now().plusHours(publicarProvaRequestDTO.getHorasExpiracao()).plusMinutes(publicarProvaRequestDTO.getMinutosExpiracao()));
        provaRepository.save(prova);
        Turma turma = turmaRepository.findByProfessorId(professor.getId())
                .orElseThrow(() -> new UserDoesntBelongException("Professor não está vinculado a uma turma"));
        String turmaName = turma.getNome();
        String disciplinaName = turma.getDisciplina().getNome();
        for (Estudante estudante : turma.getEstudantes()) {
            String mensagem = "Olá, " + estudante.getNome() + ", uma nova prova foi postada na turma " +
                    turmaName + " da disciplina " + disciplinaName + "!";
            mailService.enviarEmail(estudante.getEmail(), "Postagem de prova", mensagem);
        }
    }

    public Prova buscarPorId(Long id){
        return provaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Id da prova não existe"));
    }

    public Prova buscarPorIdEEmailDoProfessor(Long id, String email){
        return provaRepository.findByIdAndEmailProfessor(id, email)
                .orElseThrow(() -> new EntityNotFoundException("Prova não pertence a esse professor ou id da prova não existe"));
    }

    public ProvaAvaliadaResponseDTO buscarProvaAvaliada(Long id, Usuario usuario) {
        Estudante estudante = estudanteService.buscarPorId(usuario.getId());
        Prova prova = buscarPorId(id);
        List<RespostaProva> respostasProva
                = respostaProvaRepository.findByEstudanteIdAndProvaIdAndAvaliadaTrue(estudante.getId(), prova.getId());
        if (respostasProva.isEmpty()){
            throw new UserDoesntBelongException("Estudante não fez esta prova");
        }
        List<QuestaoAvaliadaResponseDTO> questaoAvaliadaResponseDTOS = mapearQuestaoAvaliadaResponseDTO(respostasProva);
        return mapearProvaAvaliadaResponseDTO(questaoAvaliadaResponseDTOS, prova, estudante);
    }

    private List<Questao> adicionarQuestoes(ProvaPutRequestDTO provaPutRequestDTO, Usuario usuario){
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPutRequestDTO.getQuestoes().size(); i++) {
            Questao questao = questaoRepository.findById(provaPutRequestDTO.getQuestoes().get(i).getId())
                    .orElseThrow(() -> new EntityNotFoundException("Id da questão não existe"));
            questoes.add(questao);
            questoes.get(i).setValor(provaPutRequestDTO.getQuestoes().get(i).getValor());
            questoes.get(i).setAlternativas(provaPutRequestDTO.getQuestoes().get(i).getAlternativas());
            questoes.get(i).setPergunta(provaPutRequestDTO.getQuestoes().get(i).getPergunta());
            questoes.get(i).setTipoQuestao(provaPutRequestDTO.getQuestoes().get(i).getTipoQuestao());
            questoes.get(i).setAtualizadoPor(usuario.getEmail());
            questoes.get(i).setRespostaCorreta(provaPutRequestDTO.getQuestoes().get(i).getRespostaCorreta());
        }
        return questoes;
    }

    private List<Questao> adicionarQuestoes(Prova prova, Professor professor, ProvaPostRequestDTO provaPostRequestDTO){
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPostRequestDTO.getQuestoes().size(); i++) {
            questoes.add(QuestaoMapper.INSTANCE.toQuestao(provaPostRequestDTO.getQuestoes().get(i)));
            if (questoes.get(i).getId() != null){
                Questao questao = questaoRepository.findById(questoes.get(i).getId())
                        .orElseThrow(() -> new EntityNotFoundException("Questão não existe"));
                List<Prova> provas = new ArrayList<>(questao.getProvas());
                provas.add(prova);
                questoes.get(i).setProvas(provas);
            } else {
                questoes.get(i).setProvas(List.of(prova));
            }
            if (questoes.get(i).getCriadoPor() == null){
                questoes.get(i).setCriadoPor(professor.getEmail());
            }
        }
        return questoes;
    }

    private List<QuestaoAvaliadaResponseDTO> mapearQuestaoAvaliadaResponseDTO(List<RespostaProva> respostasProva){
        List<QuestaoAvaliadaResponseDTO> questaoAvaliadaResponseDTOS = new ArrayList<>();
        for (RespostaProva respostaProva : respostasProva){
            questaoAvaliadaResponseDTOS.add(QuestaoAvaliadaResponseDTO.builder().questaoId(respostaProva.getQuestao().getId())
                    .pergunta(respostaProva.getQuestao().getPergunta()).resposta(respostaProva.getResposta())
                    .notaDoEstudante(respostaProva.getNota()).notaQuestao(respostaProva.getQuestao().getValor()).build());
        }
        return questaoAvaliadaResponseDTOS;
    }

    private ProvaAvaliadaResponseDTO mapearProvaAvaliadaResponseDTO(List<QuestaoAvaliadaResponseDTO> questaoAvaliadaResponseDTOS,
                                                                    Prova prova, Estudante estudante){
        double notaTotal = 0D;
        double notaPossivel = 0D;
        for (QuestaoAvaliadaResponseDTO questaoAvaliadaResponseDTO : questaoAvaliadaResponseDTOS){
            notaTotal += questaoAvaliadaResponseDTO.getNotaDoEstudante().doubleValue();
            notaPossivel += questaoAvaliadaResponseDTO.getNotaQuestao().doubleValue();
        }
        return ProvaAvaliadaResponseDTO.builder().provaId(prova.getId()).notaDoEstudante(BigDecimal.valueOf(notaTotal))
                .notaPossivel(BigDecimal.valueOf(notaPossivel)).nomeDisciplina(prova.getDisciplina().getNome()).questoesAvaliadas(questaoAvaliadaResponseDTOS)
                .build();
    }
}
