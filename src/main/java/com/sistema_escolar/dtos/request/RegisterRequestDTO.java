package com.sistema_escolar.dtos.request;

import com.sistema_escolar.utils.enums.UserRole;
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
public class RegisterRequestDTO {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private UserRole role;
}
