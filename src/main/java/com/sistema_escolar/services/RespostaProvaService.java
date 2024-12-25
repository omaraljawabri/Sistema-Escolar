package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.RespostaProvaRequestDTO;
import com.sistema_escolar.dtos.request.RespostaQuestaoRequestDTO;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.RespostaProva;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.RespostaProvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RespostaProvaService {

    private final RespostaProvaRepository respostaProvaRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;

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
    }
}
