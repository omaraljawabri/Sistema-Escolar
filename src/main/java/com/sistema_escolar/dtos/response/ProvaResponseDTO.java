package com.sistema_escolar.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProvaResponseDTO {
    private List<QuestaoResponseDTO> questoes;

    private BigDecimal valorTotal;
}
