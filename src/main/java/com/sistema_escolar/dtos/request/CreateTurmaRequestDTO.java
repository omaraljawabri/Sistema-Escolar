package com.sistema_escolar.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTurmaRequestDTO {

    @NotNull(message = "nome da turma deve ser preenchido")
    private String name;

    @NotNull(message = "disciplina da turma deve ser identificada")
    private Long disciplinaId;
}
