package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProvaPutRequestDTO {

    @NotNull
    @ArraySchema(
            schema = @Schema(implementation = QuestaoPutRequestDTO.class, description = "Lista de questões que serão enviadas"),
            minItems = 1,
            uniqueItems = true
    )
    private List<QuestaoPutRequestDTO> questoes;

    @NotNull
    @Schema(description = "valor total da prova", type = "BigDecimal", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal valorTotal;

}
