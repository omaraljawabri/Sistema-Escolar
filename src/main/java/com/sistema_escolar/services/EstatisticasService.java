package com.sistema_escolar.services;

import com.sistema_escolar.dtos.response.*;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstatisticasService {

    private final TurmaRepository turmaRepository;
    private final ProvaRepository provaRepository;
    private final NotaRepository notaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EstudanteRepository estudanteRepository;
    private final RespostaProvaRepository respostaProvaRepository;

    public EstatisticasTurmaResponseDTO estatisticasDaTurma(Long id, Usuario usuario) {
        Turma turma = turmaRepository.findByIdAndProfessorId(id, usuario.getId())
                .orElseThrow(() -> new UserNotFoundException("Professor não faz parte dessa turma ou turma não existe"));
        List<Prova> provas
                = provaRepository.findByDisciplinaIdAndEmailProfessorAndPublicadoTrue(turma.getDisciplina().getId(), turma.getProfessor().getEmail());
        List<Prova> provasExpiradas = provas.stream().filter(prova -> prova.getTempoDeExpiracao().isBefore(LocalDateTime.now())).toList();
        if (provasExpiradas.isEmpty()){
            return EstatisticasTurmaResponseDTO.builder().mediaGeral(BigDecimal.ZERO)
                    .porcentagemAprovados(BigDecimal.ZERO).estatisticasProva(Collections.emptyList()).build();
        }
        return EstatisticasTurmaResponseDTO.builder().mediaGeral(BigDecimal.valueOf(calcularMediaGeral(provas, turma)))
                .porcentagemAprovados(BigDecimal.valueOf(calcularPorcentagemGeralAcimaDeSeis(provas, turma)))
                .estatisticasProva(calcularEstatisticasPorProva(provas, turma)).build();
    }

    public EstatisticasEstudanteResponseDTO estatisticasDoEstudante(Usuario usuario) {
        double mediaGeral = 0;
        List<Nota> notasEstudante = notaRepository.findAllByEstudanteId(usuario.getId());
        BigDecimal somaTotal = notasEstudante.stream().map(Nota::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!somaTotal.equals(BigDecimal.ZERO)){
            mediaGeral = somaTotal.doubleValue()/notasEstudante.size();
        }
        List<Prova> provas = provaRepository.findByNotas(notasEstudante);
        BigDecimal valorTotal = provas.stream().map(Prova::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (valorTotal.equals(BigDecimal.ZERO)){
            return EstatisticasEstudanteResponseDTO.builder().mediaGeral(BigDecimal.valueOf(mediaGeral))
                    .porcentagemAproveitamento(BigDecimal.ZERO)
                    .estatisticasPorProva(mapearEstatisticasEstudanteProva(notasEstudante))
                    .build();
        }
        return EstatisticasEstudanteResponseDTO.builder().mediaGeral(BigDecimal.valueOf(mediaGeral))
                .porcentagemAproveitamento(BigDecimal.valueOf((somaTotal.doubleValue()/valorTotal.doubleValue())*100))
                .estatisticasPorProva(mapearEstatisticasEstudanteProva(notasEstudante))
                .build();
    }

    public EstatisticasGeraisResponseDTO estatisticasGerais() {
        return EstatisticasGeraisResponseDTO.builder().qtdDisciplinasGeral(disciplinaRepository.count())
                .estatisticasDisciplinas(mapearEstatisticasDisciplinas())
                .qtdEstudantesGeral(estudanteRepository.count())
                .qtdTurmasGeral(turmaRepository.count()).estatisticasTurmas(mapearEstatisticasTurmas())
                .build();
    }

    private Double calcularMediaGeral(List<Prova> provas, Turma turma){
        double mediaGeral = 0D;
        int numeroTotal = 0;
        for (Prova prova : provas) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                if (respostaProvaRepository.existsByProvaIdAndEstudanteId(prova.getId(), turma.getEstudantes().get(j).getId())){
                    mediaGeral += buscarNotaProvaParaEstatisticas(prova, turma.getEstudantes().get(j));
                    numeroTotal++;
                }
            }
        }
        if (numeroTotal == 0){
            numeroTotal = 1;
        }
        return mediaGeral/numeroTotal;
    }

    private Double calcularPorcentagemGeralAcimaDeSeis(List<Prova> provas, Turma turma){
        double numeroAcimaDeSeis = 0;
        double numeroTotal = 0;
        for (Prova prova : provas) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                if (buscarNotaProvaParaEstatisticas(prova, turma.getEstudantes().get(j)) >= 6) {
                    numeroAcimaDeSeis++;
                }
                numeroTotal++;
            }
        }
        if (numeroTotal == 0){
            numeroTotal = 1;
        }
        return (numeroAcimaDeSeis/numeroTotal)*100;
    }

    private List<EstatisticasProvaResponseDTO> calcularEstatisticasPorProva(List<Prova> provas, Turma turma){
        List<EstatisticasProvaResponseDTO> estatisticasProvaResponseDTOS = new ArrayList<>();
        double numeroAcimaDeSeisPorProva = 0;
        double numeroPorProva = 0;
        double mediaPorProva = 0;
        for (Prova prova : provas) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                if (respostaProvaRepository.existsByProvaIdAndEstudanteId(prova.getId(), turma.getEstudantes().get(j).getId())){
                    if (buscarNotaProvaParaEstatisticas(prova, turma.getEstudantes().get(j)) >= 6) {
                        numeroAcimaDeSeisPorProva++;
                    }
                    mediaPorProva += buscarNotaProvaParaEstatisticas(prova, turma.getEstudantes().get(j));
                    numeroPorProva++;
                }
            }
            if (numeroPorProva == 0) {
                numeroPorProva = 1;
            }
            estatisticasProvaResponseDTOS.add(EstatisticasProvaResponseDTO.builder().provaId(prova.getId())
                    .mediaTurma(BigDecimal.valueOf(mediaPorProva / numeroPorProva))
                    .porcentagemAcimaDeSeis(BigDecimal.valueOf((numeroAcimaDeSeisPorProva / numeroPorProva) * 100))
                    .build());
            numeroAcimaDeSeisPorProva = 0;
            numeroPorProva = 0;
            mediaPorProva = 0;
        }
        return estatisticasProvaResponseDTOS;
    }

    private Double buscarNotaProvaParaEstatisticas(Prova prova, Estudante estudante){
        if (notaRepository.findByEstudanteIdAndProvaId(estudante.getId(), prova.getId()).isPresent()){
            return notaRepository.findByEstudanteIdAndProvaId(estudante.getId(), prova.getId()).get().getValor().doubleValue();
        } else{
            return 0D;
        }
    }

    private List<EstatisticasEstudanteProvaResponseDTO> mapearEstatisticasEstudanteProva(List<Nota> notas){
        List<EstatisticasEstudanteProvaResponseDTO> estatisticasProvaResponseDTOS = new ArrayList<>();
        for (Nota nota: notas){
            estatisticasProvaResponseDTOS.add(EstatisticasEstudanteProvaResponseDTO.builder().provaId(nota.getProva().getId())
                    .nota(nota.getValor()).build());
        }
        return estatisticasProvaResponseDTOS;
    }

    private List<EstatisticasDisciplinasResponseDTO> mapearEstatisticasDisciplinas(){
        List<Disciplina> disciplinas = disciplinaRepository.findAll();
        List<EstatisticasDisciplinasResponseDTO> estatisticasDisciplinasResponseDTOS = new ArrayList<>();
        for (Disciplina disciplina : disciplinas){
            estatisticasDisciplinasResponseDTOS.add(EstatisticasDisciplinasResponseDTO.builder()
                    .disciplinaId(disciplina.getId()).qtdEstudantes(estudanteRepository.countByDisciplinasId(disciplina.getId()))
                    .qtdTurmas(turmaRepository.countByDisciplinaId(disciplina.getId())).build());
        }
        return estatisticasDisciplinasResponseDTOS;
    }

    private List<EstatisticasTurmasResponseDTO> mapearEstatisticasTurmas(){
        List<Turma> turmas = turmaRepository.findAll();
        List<EstatisticasTurmasResponseDTO> estatisticasTurmasResponseDTOS = new ArrayList<>();
        for (Turma turma : turmas){
            estatisticasTurmasResponseDTOS.add(EstatisticasTurmasResponseDTO.builder().turmaId(turma.getId())
                    .qtdEstudantes(estudanteRepository.countByTurmasId(turma.getId())).build());
        }
        return estatisticasTurmasResponseDTOS;
    }
}
