package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ArraySchema(
            schema = @Schema(implementation = RespostaQuestaoRequestDTO.class, description = "Lista de questões que serão respondidas"),
            minItems = 1,
            uniqueItems = true
    )
    List<RespostaQuestaoRequestDTO> respostasQuestoes;
}
