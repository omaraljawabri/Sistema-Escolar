package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "questaoId")
public class NotaRequestDTO {
    @NotNull
    @Schema(description = "Identificador único da questao", example = "1", type = "Long", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long questaoId;
    @NotNull
    @Schema(description = "Nota dada para a questão", example = "3", type = "Double", requiredMode = Schema.RequiredMode.REQUIRED)
    public Double notaQuestao;
}
