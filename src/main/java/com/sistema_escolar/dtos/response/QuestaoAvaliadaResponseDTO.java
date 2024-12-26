package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QuestaoAvaliadaResponseDTO {
    private Long questaoId;
    private String pergunta;
    private String resposta;
    private BigDecimal notaDoEstudante;
    private BigDecimal notaQuestao;
}
