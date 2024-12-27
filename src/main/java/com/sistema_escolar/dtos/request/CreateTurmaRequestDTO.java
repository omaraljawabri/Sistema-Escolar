package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTurmaRequestDTO {

    @NotNull(message = "nome da turma deve ser preenchido")
    @Schema(description = "Nome da turma que será criada", example = "Turma A", type = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "disciplina da turma deve ser identificada")
    @Schema(description = "Identificador único da disciplina em que a turma será inserida", example = "1", type = "Long",
    requiredMode = Schema.RequiredMode.REQUIRED)
    private Long disciplinaId;
}
