package com.sistema_escolar.dtos.response;

import com.sistema_escolar.utils.enums.TipoQuestao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestaoPostResponseDTO {
    private TipoQuestao tipoQuestao;

    private String pergunta;

    private List<String> alternativas;

    private BigDecimal valor;
}
