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
public class QuestaoPutResponseDTO {
    private Long questaoId;

    private TipoQuestao tipoQuestao;

    private String pergunta;

    private List<String> alternativas;

    private BigDecimal valor;

    private String criadoPor;

    private String atualizadoPor;
}
