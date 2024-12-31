package com.sistema_escolar.utils;

import com.sistema_escolar.dtos.request.*;
import com.sistema_escolar.dtos.response.*;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.utils.enums.TipoQuestao;
import com.sistema_escolar.utils.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
                .emailProfessor("professor@gmail.com").questoes(List.of(criarQuestao())).disciplina(criarDisciplina()).build();
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

    public static Turma criarTurma(){
        return Turma.builder().id(1L).name("Turma A").disciplina(criarDisciplina()).professor(criarProfessor())
                .estudantes(List.of(criarEstudante())).build();
    }

    public static QuestaoPostRequestDTO criarQuestaoPostRequestDTO(){
        return QuestaoPostRequestDTO.builder().tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .criadoPor("professor@gmail.com").respostaCorreta("Brasília").build();
    }

    public static ProvaPostRequestDTO criarProvaPostRequestDTO(){
        return ProvaPostRequestDTO.builder().questoes(List.of(criarQuestaoPostRequestDTO())).valorTotal(BigDecimal.TEN)
                .build();
    }

    public static QuestaoPutRequestDTO criarQuestaoPutRequestDTO(){
        return QuestaoPutRequestDTO.builder().id(1L).tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .atualizadoPor("professor@gmail.com").respostaCorreta("Brasília").build();
    }

    public static ProvaPutRequestDTO criarProvaPutRequestDTO(){
        return ProvaPutRequestDTO.builder().valorTotal(BigDecimal.TEN).questoes(List.of(criarQuestaoPutRequestDTO())).build();
    }

    public static PublishProvaRequestDTO criarPublishProvaRequestDTO(){
        return PublishProvaRequestDTO.builder().expirationHours(2).expirationMinutes(30)
                .build();
    }

    public static RespostaQuestaoRequestDTO criarRespostaQuestaoRequestDTO(){
        return RespostaQuestaoRequestDTO.builder().questaoId(1L).resposta("Brasília")
                .build();
    }

    public static RespostaProvaRequestDTO criarRespostaProvaRequestDTO(){
        return RespostaProvaRequestDTO.builder().respostasQuestoes(List.of(criarRespostaQuestaoRequestDTO())).build();
    }

    public static EstatisticasEstudanteResponseDTO criarEstatisticasEstudanteResponseDTO(){
        return EstatisticasEstudanteResponseDTO.builder().mediaGeral(BigDecimal.TEN).porcentagemAproveitamento(BigDecimal.valueOf(100D))
                .estatisticasPorProva(List.of(EstatisticasEstudanteProvaResponseDTO.builder().provaId(1L).nota(BigDecimal.TEN).build()))
                .build();
    }

    public static EstatisticasGeraisResponseDTO criarEstatisticasGeraisResponseDTO(){
        return EstatisticasGeraisResponseDTO.builder().qtdDisciplinasGeral(1L).qtdTurmasGeral(1L).qtdEstudantesGeral(1L)
                .estatisticasDisciplinas(List.of(EstatisticasDisciplinasResponseDTO.builder().disciplinaId(1L).qtdTurmas(1L).qtdEstudantes(1L).build()))
                .estatisticasTurmas(List.of(EstatisticasTurmasResponseDTO.builder().turmaId(1L).qtdEstudantes(1L).build())).build();
    }

    public static EstatisticasTurmaResponseDTO criarEstatisticasTurmaResponseDTO(){
        return EstatisticasTurmaResponseDTO.builder().mediaGeral(BigDecimal.TEN).porcentagemAprovados(BigDecimal.valueOf(100D))
                .estatisticasProva(List.of(EstatisticasProvaResponseDTO.builder().mediaTurma(BigDecimal.TEN)
                        .porcentagemAcimaDeSeis(BigDecimal.valueOf(100D)).provaId(1L).build())).build();
    }

    public static QuestaoResponseDTO criarQuestaoResponseDTO(){
        return QuestaoResponseDTO.builder().id(1L).tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .criadoPor("professor@gmail.com").atualizadoPor("professor@gmail.com").respostaCorreta("Brasília").build();
    }

    public static ProvaResponseDTO criarProvaResponseDTO(){
        return ProvaResponseDTO.builder().id(1L).valorTotal(BigDecimal.TEN).emailProfessor("professor@gmail.com")
                .questoes(List.of(criarQuestaoResponseDTO())).build();
    }

    public static QuestaoAvaliadaResponseDTO criarQuestaoAvaliadaResponseDTO(){
        return QuestaoAvaliadaResponseDTO.builder().questaoId(1L).pergunta("Qual a capital do Brasil?").resposta("Brasília")
                .notaDoEstudante(BigDecimal.TEN).notaQuestao(BigDecimal.TEN).build();
    }

    public static ProvaAvaliadaResponseDTO criarProvaAvaliadaResponseDTO(){
        return ProvaAvaliadaResponseDTO.builder().provaId(1L).nomeDisciplina("Geografia").notaDoEstudante(BigDecimal.TEN)
                .notaPossivel(BigDecimal.TEN).questoesAvaliadas(List.of(criarQuestaoAvaliadaResponseDTO()))
                .build();
    }

    public static void mockAuthentication(){
        Usuario usuario = new Usuario();
        usuario.setEmail("fulano@gmail.com");
        usuario.setPassword("fulano");
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
