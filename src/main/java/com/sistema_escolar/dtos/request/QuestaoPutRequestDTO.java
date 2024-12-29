package com.sistema_escolar.dtos.request;

import com.sistema_escolar.utils.enums.TipoQuestao;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class QuestaoPutRequestDTO {

    @NotNull
    @Schema(description = "Identificador único da questão", example = "1", requiredMode = Schema.RequiredMode.REQUIRED,
    type = "Long")
    private Long id;

    @NotNull
    @Schema(description = "Tipo da questão que será criada", allowableValues = {"OBJETIVA", "SUBJETIVA", "VERDADEIRO_FALSO"},
            requiredMode = Schema.RequiredMode.REQUIRED, example = "OBJETIVA")
    private TipoQuestao tipoQuestao;

    @NotNull
    @Schema(description = "Pergunta da questão que será criada", example = "Qual é a capital do Brasil?",
            requiredMode = Schema.RequiredMode.REQUIRED, type = "String")
    private String pergunta;

    @Size(min = 1)
    @ArraySchema(
            schema = @Schema(
                    type = "string",
                    description = "Texto de cada alternativa",
                    example = "A) Brasília",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED
            ),
            minItems = 1,
            uniqueItems = true
    )
    private List<String> alternativas;

    @NotNull
    @Schema(description = "Valor da questão", example = "2", type = "BigDecimal", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal valor;

    @Schema(description = "Email do usuário que atualizou a questao", type = "String", example = "beltrano@gmail.com",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String atualizadoPor;

    @Schema(description = "Resposta considerada correta para a questão", example = "Brasília", type = "String", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String respostaCorreta;

}
