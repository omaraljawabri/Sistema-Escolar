package com.sistema_escolar.services;

import com.sistema_escolar.dtos.response.*;
import com.sistema_escolar.entities.*;
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

    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final ProvaRepository provaRepository;
    private final NotaRepository notaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final EstudanteRepository estudanteRepository;

    public EstatisticasTurmaResponseDTO estatisticasDaTurma(Long id, Usuario usuario) {
        Turma turma = turmaRepository.findByIdAndProfessorId(id, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Professor não faz parte dessa turma ou turma não existe"));
        List<Prova> provas
                = provaRepository.findByDisciplinaIdAndEmailProfessorAndIsPublishedTrue(turma.getDisciplina().getId(), turma.getProfessor().getEmail());
        List<Prova> provasExpiradas = provas.stream().filter(prova -> prova.getExpirationTime().isBefore(LocalDateTime.now())).toList();
        if (provasExpiradas.isEmpty()){
            return EstatisticasTurmaResponseDTO.builder().mediaGeral(BigDecimal.ZERO)
                    .porcentagemAprovados(BigDecimal.ZERO).estatisticasProva(Collections.emptyList()).build();
        }
        return EstatisticasTurmaResponseDTO.builder().mediaGeral(BigDecimal.valueOf(calcularMediaGeral(provas, turma)))
                .porcentagemAprovados(BigDecimal.valueOf(calcularPorcentagemGeralAcimaDeSeis(provas, turma)))
                .estatisticasProva(calcularEstatisticasPorProva(provas, turma)).build();
    }

    public EstatisticasEstudanteResponseDTO estatisticasDoEstudante(Usuario usuario) {
        List<Nota> notasEstudante = notaRepository.findAllByEstudanteId(usuario.getId());
        BigDecimal mediaGeral = notasEstudante.stream().map(Nota::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Prova> provas = provaRepository.findByNotas(notasEstudante);
        BigDecimal valorTotal = provas.stream().map(Prova::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return EstatisticasEstudanteResponseDTO.builder().mediaGeral(mediaGeral)
                .porcentagemAproveitamento(BigDecimal.valueOf((mediaGeral.doubleValue()/valorTotal.doubleValue())*100))
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
        for (int i = 0; i < provas.size(); i++) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                mediaGeral += buscarNotaProvaParaEstatisticas(provas.get(i), turma.getEstudantes().get(i));
                numeroTotal++;
            }
        }
        return mediaGeral/numeroTotal;
    }

    private Double calcularPorcentagemGeralAcimaDeSeis(List<Prova> provas, Turma turma){
        double numeroAcimaDeSeis = 0;
        double numeroTotal = 0;
        for (int i = 0; i < provas.size(); i++) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                if (buscarNotaProvaParaEstatisticas(provas.get(i), turma.getEstudantes().get(i)) >= 6){
                    numeroAcimaDeSeis++;
                }
                numeroTotal++;
            }
        }
        return (numeroAcimaDeSeis/numeroTotal)*100;
    }

    private List<EstatisticasProvaResponseDTO> calcularEstatisticasPorProva(List<Prova> provas, Turma turma){
        List<EstatisticasProvaResponseDTO> estatisticasProvaResponseDTOS = new ArrayList<>();
        double numeroAcimaDeSeisPorProva = 0;
        double numeroPorProva = 0;
        double mediaPorProva = 0;
        for (int i = 0; i < provas.size(); i++) {
            for (int j = 0; j < turma.getEstudantes().size(); j++) {
                if (buscarNotaProvaParaEstatisticas(provas.get(i), turma.getEstudantes().get(i)) >= 6){
                    numeroAcimaDeSeisPorProva++;
                }
                mediaPorProva+= buscarNotaProvaParaEstatisticas(provas.get(i), turma.getEstudantes().get(i));
                numeroPorProva++;
            }
            estatisticasProvaResponseDTOS.add(EstatisticasProvaResponseDTO.builder().provaId(provas.get(i).getId())
                    .mediaTurma(BigDecimal.valueOf(mediaPorProva/numeroPorProva))
                    .porcentagemAcimaDeSeis(BigDecimal.valueOf((numeroAcimaDeSeisPorProva/numeroPorProva)*100))
                    .build());
            numeroAcimaDeSeisPorProva = 0;
            numeroPorProva = 0;
            mediaPorProva = 0;
        }
        return estatisticasProvaResponseDTOS;
    }

    private Double buscarNotaProvaParaEstatisticas(Prova prova, Estudante estudante){
        if (notaRepository.findByEstudanteIdAndProvaId(prova.getId(), estudante.getId()).isPresent()){
            return notaRepository.findByEstudanteIdAndProvaId(prova.getId(), estudante.getId()).get().getValor().doubleValue();
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
