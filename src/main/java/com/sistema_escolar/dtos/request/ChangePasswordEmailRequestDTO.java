package com.sistema_escolar.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordEmailRequestDTO {
    @Email
    @NotNull
    @Schema(description = "E-mail do usuário", type = "String", example = "fulano@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
