package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CriarDisciplinaRequestDTO {
    @NotNull(message = "nome da disciplina deve ser preenchido")
    @Schema(description = "Nome da disciplina que será criada", example = "Geografia", type = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;
}
