package com.sistema_escolar.services;

import com.sistema_escolar.dtos.response.EstatisticasProvaResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.Turma;
import com.sistema_escolar.entities.Usuario;
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
}
