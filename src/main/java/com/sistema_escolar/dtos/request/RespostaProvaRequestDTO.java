package com.sistema_escolar.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RespostaProvaRequestDTO {
    @NotNull
    List<RespostaQuestaoRequestDTO> respostasQuestoes;
}
