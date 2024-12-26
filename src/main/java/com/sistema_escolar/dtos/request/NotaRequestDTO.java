package com.sistema_escolar.dtos.request;

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
    public Long questaoId;
    @NotNull
    public Double notaQuestao;
}
