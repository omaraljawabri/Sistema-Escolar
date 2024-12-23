package com.sistema_escolar.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateDisciplinaRequestDTO {
    @NotNull(message = "nome da disciplina deve ser preenchido")
    private String name;
}
