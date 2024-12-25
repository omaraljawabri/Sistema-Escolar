package com.sistema_escolar.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProvaPostResponseDTO {
    private List<QuestaoPostResponseDTO> questoes;

    private BigDecimal valorTotal;

    private String emailProfessor;
}
