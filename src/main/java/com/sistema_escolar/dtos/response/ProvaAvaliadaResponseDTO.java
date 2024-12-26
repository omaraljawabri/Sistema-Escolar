package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProvaAvaliadaResponseDTO {
    private Long provaId;
    private String nomeDisciplina;
    private BigDecimal notaDoEstudante;
    private BigDecimal notaPossivel;
    private List<QuestaoAvaliadaResponseDTO> questoesAvaliadas;
}
