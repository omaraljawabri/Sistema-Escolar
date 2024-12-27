package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PublishProvaRequestDTO {
    @Schema(description = "Número de horas que a prova ficará disponível", example = "1", type = "Integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public Integer expirationHours;
    @Schema(description = "Número de minutos que a prova ficará disponível", example = "30", type = "Integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public Integer expirationMinutes;
}
