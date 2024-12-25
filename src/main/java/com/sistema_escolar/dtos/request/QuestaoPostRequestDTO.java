package com.sistema_escolar.dtos.request;

import com.sistema_escolar.utils.enums.TipoQuestao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestaoPostRequestDTO {

    @NotNull
    private TipoQuestao tipoQuestao;

    @NotNull
    private String pergunta;

    @Size(min = 1)
    private List<String> alternativas;

    @NotNull
    private BigDecimal valor;

    private String criadoPor;

}
