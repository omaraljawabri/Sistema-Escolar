package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.response.ProvaPostResponseDTO;
import com.sistema_escolar.dtos.response.ProvaPutResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoPostResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoPutResponseDTO;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final QuestaoRepository questaoRepository;

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
                    .pergunta(provaPostRequestDTO.getQuestoes().get(i).getPergunta()).build());
            questoes.get(i).setProva(prova);
        }
        prova.setQuestoes(questoes);
        prova.setDisciplina(professor.getDisciplina());
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoPostResponseDTO> questaoPostResponseDTOS = new ArrayList<>();
        for (Questao questao : savedQuestoes) {
            questaoPostResponseDTOS.add(QuestaoPostResponseDTO.builder().tipoQuestao(questao.getTipoQuestao())
                    .valor(questao.getValor()).alternativas(questao.getAlternativas())
                    .pergunta(questao.getPergunta()).build());
        }
        return ProvaPostResponseDTO.builder().valorTotal(savedProva.getValorTotal()).questoes(questaoPostResponseDTOS).build();
    }

    @Transactional
    public ProvaPutResponseDTO updateProva(ProvaPutRequestDTO provaPutRequestDTO) {
        Prova prova = provaRepository.findById(provaPutRequestDTO.getProvaId())
                .orElseThrow(() -> new RuntimeException("Id da prova não existe"));
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaPutRequestDTO.getQuestoes().size(); i++) {
            Questao questao = questaoRepository.findById(provaPutRequestDTO.getQuestoes().get(i).getQuestaoId())
                    .orElseThrow(() -> new RuntimeException("Id da questão não existe"));
            questoes.add(questao);
            questoes.get(i).setValor(provaPutRequestDTO.getQuestoes().get(i).getValor());
            questoes.get(i).setAlternativas(provaPutRequestDTO.getQuestoes().get(i).getAlternativas());
            questoes.get(i).setPergunta(provaPutRequestDTO.getQuestoes().get(i).getPergunta());
            questoes.get(i).setTipoQuestao(provaPutRequestDTO.getQuestoes().get(i).getTipoQuestao());
        }
        prova.setValorTotal(provaPutRequestDTO.getValorTotal());
        prova.setQuestoes(questoes);
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoPutResponseDTO> questaoPutResponseDTOS = new ArrayList<>();
        for (Questao questao : savedQuestoes) {
            questaoPutResponseDTOS.add(QuestaoPutResponseDTO.builder().tipoQuestao(questao.getTipoQuestao())
                    .valor(questao.getValor()).alternativas(questao.getAlternativas())
                    .pergunta(questao.getPergunta()).questaoId(questao.getId()).build());
        }
        return ProvaPutResponseDTO.builder().provaId(savedProva.getId()).valorTotal(savedProva.getValorTotal())
                .questoes(questaoPutResponseDTOS).build();
    }
}
