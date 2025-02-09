package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.QuestionErrorException;
import com.sistema_escolar.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final ProfessorService professorService;
    private final ProvaService provaService;
    private final QuestaoRepository questaoRepository;
    private final RespostaProvaRepository respostaProvaRepository;
    private final RespostaProvaService respostaProvaService;
    private final EstudanteService estudanteService;
    private final MailService mailService;

    @Transactional
    public NotaResponseDTO avaliarProva(Long id, List<NotaRequestDTO> notaRequestDTO, Usuario usuario, Long estudanteId) {
        Estudante estudante = estudanteService.buscarPorId(estudanteId);
        Professor professor = professorService.buscarPorId(usuario.getId());
        Prova prova = provaService.buscarPorIdEEmailDoProfessor(id, professor.getEmail());
        List<NotaRequestDTO> notasOrdenadas
                = notaRequestDTO.stream().sorted(Comparator.comparing(NotaRequestDTO::getQuestaoId)).toList();
        Double notaTotal = adicionarRespostasProva(prova, notasOrdenadas, estudante);
        mailService.enviarEmail(estudante.getEmail(), "Recebimento de nota",
                String.format("Olá, uma nova nota sua foi publicada na disciplina %s pelo professor %s %s", professor.getDisciplina().getNome(), professor.getNome(), professor.getSobrenome()));
        return NotaResponseDTO.builder().notaProva(notaTotal).build();
    }

    private void salvarNota(Prova prova, Estudante estudante, Double notaTotal){
        Nota nota = Nota.builder().prova(prova).valor(BigDecimal.valueOf(notaTotal)).estudante(estudante).build();
        notaRepository.save(nota);
    }

    private Double adicionarRespostasProva(Prova prova, List<NotaRequestDTO> notasOrdenadas, Estudante estudante){
        Double notaTotal = 0d;
        List<RespostaProva> respostasProva = new ArrayList<>();
        for (int i = 0; i < prova.getQuestoes().size(); i++) {
            if (prova.getQuestoes().get(i).getValor().compareTo(BigDecimal.valueOf(notasOrdenadas.get(i).getNotaQuestao())) < 0) {
                throw new QuestionErrorException("Nota da questão é maior do que seu valor máximo");
            }
            notaTotal += notasOrdenadas.get(i).getNotaQuestao();
            Questao questao = questaoRepository.findById(notasOrdenadas.get(i).getQuestaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Id da questão não existe"));
            RespostaProva respostaProva = respostaProvaRepository.findByQuestaoIdAndProvaIdAndEstudanteId(questao.getId(), prova.getId(), estudante.getId())
                    .orElseThrow(() -> new QuestionErrorException("Questão não foi respondida pelo estudante na prova com id passado"));
            respostaProva.setNota(BigDecimal.valueOf(notasOrdenadas.get(i).getNotaQuestao()));
            respostaProva.setAvaliada(true);
            respostasProva.add(respostaProva);
        }
        salvarNota(prova, estudante, notaTotal);
        respostaProvaService.salvarRespostasProva(respostasProva);
        return notaTotal;
    }

}
