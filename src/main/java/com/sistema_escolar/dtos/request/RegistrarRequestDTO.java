package com.sistema_escolar.dtos.request;

import com.sistema_escolar.utils.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegistrarRequestDTO {
    @NotNull
    @Email
    @Schema(description = "E-mail do usuário", type = "String", example = "fulano@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull
    @Schema(description = "Senha do usuário", type = "String", example = "fulano", requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;

    @NotNull
    @Schema(description = "Primeiro nome do usuário", type = "String", example = "Fulano", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotNull
    @Schema(description = "Sobrenome do usuário", type = "String", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sobrenome;

    @NotNull
    @Schema(description = "Tipo de conta do usuário", example = "ADMIN", allowableValues = {"ADMIN", "PROFESSOR", "ESTUDANTE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRole role;
}
