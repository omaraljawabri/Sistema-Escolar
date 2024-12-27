package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordRequestDTO {
    @NotNull
    @Schema(description = "Nova senha do usuário", type = "String", example = "fulano10", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
