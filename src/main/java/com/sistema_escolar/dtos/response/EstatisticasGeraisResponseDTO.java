package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EstatisticasGeraisResponseDTO {
    private long qtdDisciplinasGeral;
    private List<EstatisticasDisciplinasResponseDTO> estatisticasDisciplinas;
    private long qtdTurmasGeral;
    private List<EstatisticasTurmasResponseDTO> estatisticasTurmas;
    private long qtdEstudantesGeral;
}
