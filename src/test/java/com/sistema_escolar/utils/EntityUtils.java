package com.sistema_escolar.utils;

import com.sistema_escolar.dtos.request.*;
import com.sistema_escolar.dtos.response.*;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.utils.enums.TipoQuestao;
import com.sistema_escolar.utils.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EntityUtils {

    public static Usuario criarUsuario(){
        return Usuario.builder().id(1L).email("fulano@example.com").senha("fulano").role(UserRole.ADMIN)
                .nome("Fulano").sobrenome("Silva").tempoDeExpiracaoCodigo(LocalDateTime.now().plusHours(3))
                .codigoDeVerificacao("acde070d-8c4c-4f0d-9d8a-162843c10333").verificado(false).build();
    }

    public static RegistrarRequestDTO criarRegisterRequestDTO(){
        return RegistrarRequestDTO.builder().email("fulano@example.com").nome("Fulano").sobrenome("Silva")
                .senha("fulano").role(UserRole.ADMIN).build();
    }

    public static LoginRequestDTO criarLoginRequestDTO(){
        return LoginRequestDTO.builder().email("fulano@example.com").senha("fulano")
                .build();
    }

    public static LoginResponseDTO criarLoginResponseDTO(){
        return LoginResponseDTO.builder().email("fulano@example.com")
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();
    }

    public static RedefinirSenha criarRedefinirSenha(){
        return RedefinirSenha.builder().id(1L).codigoDeVerificacao("acde070d-8c4c-4f0d-9d8a-162843c10334")
                .tempoDeExpiracaoCodigo(LocalDateTime.now().plusHours(2)).usuario(criarUsuario())
                .build();
    }

    public static Disciplina criarDisciplina(){
        return Disciplina.builder().id(1L).nome("Geografia").build();
    }

    public static Estudante criarEstudante(){
        Estudante estudante = new Estudante("ciclano@example.com", "ciclano", UserRole.ESTUDANTE,
                "acde070d-8c4c-4f0d-9d8a-162843c10334", LocalDateTime.now().plusHours(2),
                false, "Ciclano", "Sousa");
        estudante.setId(1L);
        return estudante;
    }

    public static Professor criarProfessor(){
        Professor professor = new Professor("professor@example.com", "professor", UserRole.PROFESSOR, "acde070d-8c4c-4f0d-9d8a-162843c10334",
                LocalDateTime.now().plusHours(2), false, "Professor", "Santos");
        professor.setDisciplina(criarDisciplina());
        professor.setId(1L);
        return professor;
    }

    public static Prova criarProva(){
        return Prova.builder().id(1L).valorTotal(BigDecimal.TEN).publicado(true).tempoDeExpiracao(LocalDateTime.now().plusHours(2))
                .emailProfessor("professor@example.com").questoes(List.of(criarQuestao())).disciplina(criarDisciplina()).build();
    }

    public static Questao criarQuestao(){
        return Questao.builder().id(1L).tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .criadoPor("professor@example.com").atualizadoPor(null).respostaCorreta("Brasília").build();
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
        return Turma.builder().id(1L).nome("Turma A").disciplina(criarDisciplina()).professor(criarProfessor())
                .estudantes(List.of(criarEstudante())).build();
    }

    public static QuestaoPostRequestDTO criarQuestaoPostRequestDTO(){
        return QuestaoPostRequestDTO.builder().tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .criadoPor("professor@example.com").respostaCorreta("Brasília").build();
    }

    public static ProvaPostRequestDTO criarProvaPostRequestDTO(){
        return ProvaPostRequestDTO.builder().questoes(List.of(criarQuestaoPostRequestDTO())).valorTotal(BigDecimal.TEN)
                .build();
    }

    public static QuestaoPutRequestDTO criarQuestaoPutRequestDTO(){
        return QuestaoPutRequestDTO.builder().id(1L).tipoQuestao(TipoQuestao.OBJETIVA).pergunta("Qual a capital do Brasil?")
                .alternativas(List.of("A) Brasília", "B) Goiânia", "C) São Paulo", "D) Manaus")).valor(BigDecimal.TWO)
                .atualizadoPor("professor@example.com").respostaCorreta("Brasília").build();
    }

    public static ProvaPutRequestDTO criarProvaPutRequestDTO(){
        return ProvaPutRequestDTO.builder().valorTotal(BigDecimal.TEN).questoes(List.of(criarQuestaoPutRequestDTO())).build();
    }

    public static PublicarProvaRequestDTO criarPublishProvaRequestDTO(){
        return PublicarProvaRequestDTO.builder().horasExpiracao(2).minutosExpiracao(30)
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
                .criadoPor("professor@example.com").atualizadoPor("professor@example.com").respostaCorreta("Brasília").build();
    }

    public static ProvaResponseDTO criarProvaResponseDTO(){
        return ProvaResponseDTO.builder().id(1L).valorTotal(BigDecimal.TEN).emailProfessor("professor@example.com")
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
        usuario.setEmail("fulano@example.com");
        usuario.setSenha("fulano");
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static QuestaoRespondidaResponseDTO criarQuestaoRespondidaResponseDTO(){
        return QuestaoRespondidaResponseDTO.builder().pergunta("Qual a capital do Brasil?").resposta("Brasília").build();
    }

    public static ProvaRespondidaResponseDTO criarProvaRespondidaResponseDTO(){
        return ProvaRespondidaResponseDTO.builder().estudanteId(1L).nomeEstudante("Ciclano Sousa")
                .questoesRespondidas(List.of(criarQuestaoRespondidaResponseDTO())).build();
    }

    public static Usuario criarAdminIT(){
        return Usuario.builder().email("fulano@example.com").senha(new BCryptPasswordEncoder().encode("fulano")).role(UserRole.ADMIN)
                .nome("Fulano").sobrenome("Silva").build();
    }

    public static Estudante criarEstudanteIT(){
        return new Estudante("ciclano@example.com", new BCryptPasswordEncoder().encode("ciclano"), UserRole.ESTUDANTE,
                null, null, true, "Ciclano",
                "Sousa");
    }

    public static Professor criarProfessorIT(){
        return new Professor("professor@example.com", new BCryptPasswordEncoder().encode("professor"), UserRole.PROFESSOR, null,
                null, true, "Professor", "Santos");
    }
}
