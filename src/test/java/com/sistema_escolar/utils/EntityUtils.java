package com.sistema_escolar.utils;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.utils.enums.TipoQuestao;
import com.sistema_escolar.utils.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    public static Disciplina criarDisciplina(){
        return Disciplina.builder().id(1L).name("Geografia").build();
    }

    public static Estudante criarEstudante(){
        Estudante estudante = new Estudante("ciclano@gmail.com", "ciclano", UserRole.ESTUDANTE,
                "acde070d-8c4c-4f0d-9d8a-162843c10334", LocalDateTime.now().plusHours(2),
                false, "Ciclano", "Sousa");
        estudante.setId(1L);
        return estudante;
    }

    public static Professor criarProfessor(){
        Professor professor = new Professor("professor@gmail.com", "professor", UserRole.PROFESSOR, "acde070d-8c4c-4f0d-9d8a-162843c10334",
                LocalDateTime.now().plusHours(2), false, "Professor", "Santos");
        professor.setDisciplina(criarDisciplina());
        professor.setId(1L);
        return professor;
    }

    public static Prova criarProva(){
        return Prova.builder().id(1L).valorTotal(BigDecimal.TEN).isPublished(true).expirationTime(LocalDateTime.now().plusHours(2))
                .emailProfessor("professor@gmail.com").questoes(List.of(criarQuestao())).build();
    }

    public static Questao criarQuestao(){
        return Questao.builder().id(1L).tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .criadoPor("professor@gmail.com").atualizadoPor(null).respostaCorreta("Brasília").build();
    }

    public static RespostaProva criarRespostaProva(){
        return RespostaProva.builder().id(1L).resposta("Brasília").estudante(criarEstudante()).questao(criarQuestao())
                .prova(criarProva()).nota(BigDecimal.TWO).avaliada(true).respondida(true).build();
    }

    public static NotaRequestDTO criarNotaRequestDTO(){
        return NotaRequestDTO.builder().questaoId(1L).notaQuestao(2D).build();
    }

    public static Nota criarNota(){
        return Nota.builder().id(1L).valor(BigDecimal.TEN).estudante(criarEstudante()).prova(criarProva())
                .build();
    }

}
