package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurmaRequestDTO {
    @NotNull
    @Schema(description = "Identificador único da turma que o código será gerado", example = "1", type = "Long",
    requiredMode = Schema.RequiredMode.REQUIRED)
    private Long turmaId;
}
