package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CodeRequestDTO {
    @NotNull
    @Schema(description = "Código de identificação da turma", example = "?26@4Y2615A", type = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codigo;
}
