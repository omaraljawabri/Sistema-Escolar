package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Log4j2
class NotaControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private RespostaProvaRepository respostaProvaRepository;


    private String gerarTokenJWT(Usuario usuario, String password) {
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/auth/login", new LoginRequestDTO(usuarioSalvo.getEmail(), password), LoginResponseDTO.class);
        if (loginResponseDTO.getBody() != null) {
            return loginResponseDTO.getBody().getToken();
        }
        return null;
    }

    private void adicionarDependencias(){
        Estudante estudanteSalvo = usuarioRepository.save(criarEstudanteIT());
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
        Turma turma = criarTurma();
        turma.setId(null);
        turma.setEstudantes(null);
        turmaRepository.save(turma);
        Questao questao = criarQuestao();
        questao.setId(null);
        Questao questaoSalva = questaoRepository.save(questao);
        Prova prova = criarProva();
        prova.setId(null);
        Prova provaSalva = provaRepository.save(prova);
        questaoSalva.setProvas(List.of(provaSalva));
        questaoRepository.save(questaoSalva);
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setId(null);
        respostaProva.setEstudante(estudanteSalvo);
        respostaProvaRepository.save(respostaProva);
    }

    @Test
    @DisplayName("avaliarProva deve retornar uma NotaResponseDTO e um http status 200 quando a prova for avaliada com sucesso")
    void avaliarProva_RetornaNotaResponseDTOEStatus200_QuandoProvaEAvaliadaComSucesso() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notaReponseDTO.getBody()).isNotNull();
        assertThat(notaReponseDTO.getBody().getNotaProva()).isEqualTo(2D);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 404 quando id do estudante passado não existir")
    void avaliarProva_Retorna404_QuandoEstudanteIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 1L, 5L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 403 quando o id do professor que tentar avaliar uma prova não existir")
    void avaliarProva_Retorna403_QuandoProfessorIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        usuarioRepository.deleteById(1L);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 403 quando prova não pertencer ao professor ou id da prova não existir")
    void avaliarProva_Retorna403_QuandoProvaNaoPertencerAoProfessorOuProvaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 5L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 400 quando nota da questão passada for maior que seu valor máximo")
    void avaliarProva_Retorna400_QuandoNotaDaQuestaoEMaiorQueSeuValorMaximo(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        NotaRequestDTO notaRequestDTO = criarNotaRequestDTO();
        notaRequestDTO.setNotaQuestao(10D);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(notaRequestDTO), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 404 quando id da questão passada não existir")
    void avaliarProva_Retorna404_QuandoQuestaoIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        NotaRequestDTO notaRequestDTO = criarNotaRequestDTO();
        notaRequestDTO.setQuestaoId(2L);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(notaRequestDTO), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 400 quando questão não tiver sido respondida pelo estudante na prova com id passado")
    void avaliarProva_Retorna400_QuandoQuestaoNaoFoiRespondidaPeloEstudanteNaProvaComIdPassado(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        professor.setDisciplina(disciplinaRepository.findById(1L).get());
        usuarioRepository.save(professor);
        respostaProvaRepository.deleteById(1L);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("avaliarProva deve retornar um http status 403 quando o usuário que tentar avaliar uma prova não for do tipo PROFESSOR")
    void avaliarProva_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<NotaResponseDTO> notaReponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/nota/prova/{id}/{estudanteId}", new HttpEntity<>(List.of(criarNotaRequestDTO()), headers), NotaResponseDTO.class, 1L, 2L);
        assertThat(notaReponseDTO).isNotNull();
        assertThat(notaReponseDTO.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}