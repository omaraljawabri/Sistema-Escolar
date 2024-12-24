package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
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
    public ProvaResponseDTO createProva(ProvaRequestDTO provaRequestDTO, Usuario usuario) {
        Professor professor = professorRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não está cadastrado"));
        if (turmaRepository.findByProfessorId(professor.getId()).isEmpty()){
            throw new RuntimeException("Professor deve estar vinculado a uma turma para criar uma prova");
        }
        Prova prova = Prova.builder().disciplina(professor.getDisciplina()).valorTotal(provaRequestDTO.getValorTotal()).build();
        List<Questao> questoes = new ArrayList<>();
        for (int i = 0; i < provaRequestDTO.getQuestoes().size(); i++) {
            questoes.add(Questao.builder().alternativas(provaRequestDTO.getQuestoes().get(i).getAlternativas())
                    .valor(provaRequestDTO.getQuestoes().get(i).getValor()).tipoQuestao(provaRequestDTO.getQuestoes().get(i).getTipoQuestao())
                    .pergunta(provaRequestDTO.getQuestoes().get(i).getPergunta()).build());
            questoes.get(i).setProva(prova);
        }
        prova.setQuestoes(questoes);
        prova.setDisciplina(professor.getDisciplina());
        Prova savedProva = provaRepository.save(prova);
        List<Questao> savedQuestoes = questaoRepository.saveAll(questoes);
        List<QuestaoResponseDTO> questaoResponseDTOS = new ArrayList<>();
        for (int i = 0; i < savedQuestoes.size(); i++) {
            questaoResponseDTOS.add(QuestaoResponseDTO.builder().tipoQuestao(savedQuestoes.get(i).getTipoQuestao())
                    .valor(savedQuestoes.get(i).getValor()).alternativas(savedQuestoes.get(i).getAlternativas())
                    .pergunta(savedQuestoes.get(i).getPergunta()).build());
        }
        return ProvaResponseDTO.builder().valorTotal(savedProva.getValorTotal()).questoes(questaoResponseDTOS).build();
    }
}
