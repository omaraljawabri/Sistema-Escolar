package com.sistema_escolar.utils;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.RedefinirSenha;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.utils.enums.UserRole;

import java.time.LocalDateTime;

public class EntityUtils {

    public static Usuario criarUsuario(){
        return Usuario.builder().id(1L).email("fulano@gmail.com").password("fulano").role(UserRole.ADMIN)
                .firstName("Fulano").lastName("Silva").codeExpirationTime(LocalDateTime.now().plusHours(3))
                .verificationCode("acde070d-8c4c-4f0d-9d8a-162843c10333").isVerified(false).build();
    }

    public static RegisterRequestDTO criarRegisterRequestDTO(){
        return RegisterRequestDTO.builder().email("fulano@gmail.com").firstName("Fulano").lastName("Silva")
                .password("fulano").role(UserRole.ADMIN).build();
    }

    public static LoginRequestDTO criarLoginRequestDTO(){
        return LoginRequestDTO.builder().email("fulano@gmail.com").password("fulano")
                .build();
    }

    public static LoginResponseDTO criarLoginResponseDTO(){
        return LoginResponseDTO.builder().email("fulano@gmail.com")
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();
    }

    public static RedefinirSenha criarRedefinirSenha(){
        return RedefinirSenha.builder().id(1L).verificationCode("acde070d-8c4c-4f0d-9d8a-162843c10334")
                .expirationCodeTime(LocalDateTime.now().plusHours(2)).usuario(criarUsuario())
                .build();
    }
}
