package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddTurmaRequestDTO {
    @NotNull
    @Schema(description = "E-mail do usuário que será adicionado na turma", example = "ciclano@gmail.com", type = "String",
    requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull
    @Schema(description = "Identificador único da turma em que o usuário será adicionado", example = "1", type = "Long",
    requiredMode = Schema.RequiredMode.REQUIRED)
    private Long turmaId;
}
