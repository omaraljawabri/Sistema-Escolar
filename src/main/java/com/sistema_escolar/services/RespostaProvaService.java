package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.RespostaProvaRequestDTO;
import com.sistema_escolar.dtos.request.RespostaQuestaoRequestDTO;
import com.sistema_escolar.dtos.response.ProvaRespondidaResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoRespondidaResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class RespostaProvaService {

    private final RespostaProvaRepository respostaProvaRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;
    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;
    private final MailService mailService;

    @Transactional
    public void responderProva(Long id, RespostaProvaRequestDTO respostaProvaRequestDTO, Usuario usuario) {
        Estudante estudante = estudanteRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Estudante não está cadastrado"));
        Prova prova = provaRepository.findById(id).orElseThrow(() -> new RuntimeException("Id da prova não existe"));
        if (!prova.getIsPublished() || prova.getExpirationTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("O tempo de prova já foi encerrado ou a prova não foi publicada ainda");
        }
        if (!respostaProvaRepository.findByEstudanteIdAndProvaId(estudante.getId(), id).isEmpty() &&
        respostaProvaRepository.findByEstudanteIdAndProvaId(estudante.getId(), id).getFirst().getRespondida()){
            throw new RuntimeException("Estudante já respondeu a esta prova");
        }
        List<RespostaProva> respostaProva = new ArrayList<>();
        for(RespostaQuestaoRequestDTO questao: respostaProvaRequestDTO.getRespostasQuestoes()){
            if (questaoRepository.findById(questao.getQuestaoId()).isEmpty()
                    || !questaoRepository.existsByIdAndProvasId(questao.getQuestaoId(), id)){
                throw new RuntimeException("Id da questão não existe ou questão não pertence a esta prova");
            }
            respostaProva.add(RespostaProva.builder().resposta(questao.getResposta()).estudante(estudante)
                    .questao(questaoRepository.findById(questao.getQuestaoId()).get()).prova(prova)
                    .respondida(true).build());
        }
        respostaProvaRepository.saveAll(respostaProva);
        String mensagem = String.format("O aluno %s enviou a prova da disciplina de %s, entre na plataforma para começar a correção!", estudante.getFirstName(), prova.getDisciplina().getName());
        mailService.sendEmail(prova.getEmailProfessor(), "Envio de prova", mensagem);
    }

    public List<ProvaRespondidaResponseDTO> provasRespondidas(Usuario usuario, Long provaId) {
        Professor professor = professorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Professor não está cadastrado"));
        if (provaRepository.findByIdAndEmailProfessor(provaId, professor.getEmail()).isEmpty()){
            throw new RuntimeException("Prova não pertence a esse usuário");
        }
        List<RespostaProva> respostasProva = respostaProvaRepository.findAllByProvaIdAndRespondidaTrue(provaId);
        List<ProvaRespondidaResponseDTO> provaRespondidaResponseDTOS = new ArrayList<>();
        List<QuestaoRespondidaResponseDTO> questaoRespondidaResponseDTOS = new ArrayList<>();
        for (int i = 0; i < respostasProva.size(); i++) {
            questaoRespondidaResponseDTOS.add(QuestaoRespondidaResponseDTO.builder()
                    .pergunta(questaoRepository.findById(respostasProva.get(i).getQuestao().getId()).get().getPergunta())
                    .resposta(respostasProva.get(i).getResposta())
                    .build());
            if ((respostasProva.size()-1 > i && !Objects.equals(respostasProva.get(i).getEstudante().getId(),
                    respostasProva.get(i+1).getEstudante().getId())) || respostasProva.size()-1 == i) {
                provaRespondidaResponseDTOS.add(ProvaRespondidaResponseDTO.builder().estudanteId(respostasProva.get(i).getEstudante().getId())
                        .nomeEstudante(String.format("%s %s", respostasProva.get(i).getEstudante().getFirstName(),
                                respostasProva.get(i).getEstudante().getLastName()))
                        .questoesRespondidas(new ArrayList<>(questaoRespondidaResponseDTOS)).build());
                questaoRespondidaResponseDTOS.clear();
            }
        }
        return provaRespondidaResponseDTOS;
    }
}
