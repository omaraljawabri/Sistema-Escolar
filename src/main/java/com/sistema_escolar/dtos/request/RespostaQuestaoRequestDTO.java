package com.sistema_escolar.dtos.request;

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
    private Long questaoId;

    @NotNull
    private String resposta;
}
