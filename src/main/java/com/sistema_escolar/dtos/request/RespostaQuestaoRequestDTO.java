package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RespostaQuestaoRequestDTO {
    @NotNull
    @Schema(description = "Identificador único da questão", example = "1", requiredMode = Schema.RequiredMode.REQUIRED,
    type = "Long")
    private Long questaoId;

    @NotNull
    @Schema(description = "Resposta da questão", example = "Goiânia", requiredMode = Schema.RequiredMode.REQUIRED,
    type = "String")
    private String resposta;
}
