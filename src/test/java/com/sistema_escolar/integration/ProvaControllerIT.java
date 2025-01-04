package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Log4j2
class ProvaControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private ProvaRepository provaRepository;

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

    private void adicionarDependenciasPost(){
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
        Turma turma = criarTurma();
        turma.setId(null);
        turma.setEstudantes(null);
        turmaRepository.save(turma);
    }

    private void adicionarDependenciasPut(){
        adicionarDependenciasPost();
        Questao questao = criarQuestao();
        questao.setId(null);
        questaoRepository.save(questao);
        Prova prova = criarProva();
        prova.setPublicado(false);
        prova.setTempoDeExpiracao(null);
        prova.setId(null);
        provaRepository.save(prova);
    }

    private void adicionarDependenciasGet(){
        adicionarDependenciasPut();
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setId(null);
        respostaProvaRepository.save(respostaProva);
    }

    @Test
    @DisplayName("criarProva deve cadastrar uma prova no sistema e retornar http status 200 quando bem sucedido")
    void criarProva_CadastraUmaProvaERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.postForEntity(rootUrl + "/prova", new HttpEntity<>(criarProvaPostRequestDTO(), headers), ProvaResponseDTO.class);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaResponse.getBody()).isNotNull();
        assertThat(provaResponse.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("criarProva deve retornar um http status 403 quando o professor não existir")
    void criarProva_Retorna403_QuandoProfessorNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        usuarioRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.postForEntity(rootUrl + "/prova", new HttpEntity<>(criarProvaPostRequestDTO(), headers), ProvaResponseDTO.class);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("criarProva deve retornar um http status 400 quando professor não estiver vinculado a nenhuma turma")
    void criarProva_Retorna400_QuandoProfesorNaoEstiverVinculadoANenhumaTurma(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPost();
        turmaRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.postForEntity(rootUrl + "/prova", new HttpEntity<>(criarProvaPostRequestDTO(), headers), ProvaResponseDTO.class);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("criarProva deve retornar um http status 404 quando a questão adicionada a prova tiver id e não existir no banco de dados")
    void criarProva_Retorna404_QuandoQuestaoTiverIdENaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ProvaPostRequestDTO provaPostRequestDTO = criarProvaPostRequestDTO();
        provaPostRequestDTO.getQuestoes().getFirst().setId(2L);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.postForEntity(rootUrl + "/prova", new HttpEntity<>(provaPostRequestDTO, headers), ProvaResponseDTO.class);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("criarProva deve retornar um http status 403 quando o usuário que está tentando criar a prova não for do tipo PROFESSOR")
    void criarProva_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.postForEntity(rootUrl + "/prova", new HttpEntity<>(criarProvaPostRequestDTO(), headers), ProvaResponseDTO.class);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("atualizarProva deve atualizar uma prova e retornar um http status 200 quando bem sucedido")
    void atualizarProva_AtualizaProvaERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/{id}", HttpMethod.PUT, new HttpEntity<>(criarProvaPutRequestDTO(), headers), ProvaResponseDTO.class, 1L);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaResponse.getBody()).isNotNull();
        assertThat(provaResponse.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("atualizarProva deve retornar um http status 404 quando o id da prova não existir ou ela não pertencer ao professor")
    void atualizarProva_Retorna404_QuandoProvaIdNaoExistirOuNaoPertencerAoProfessor(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        provaRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/{id}", HttpMethod.PUT, new HttpEntity<>(criarProvaPutRequestDTO(), headers), ProvaResponseDTO.class, 1L);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("atualizarProva deve retornar um http status 404 quando o id da questão a ser atualizada não existir")
    void atualizarProva_Retorna404_QuandoQuestaoIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ProvaPutRequestDTO provaPutRequestDTO = criarProvaPutRequestDTO();
        provaPutRequestDTO.getQuestoes().getFirst().setId(2L);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/{id}", HttpMethod.PUT, new HttpEntity<>(provaPutRequestDTO, headers), ProvaResponseDTO.class, 1L);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("atualizarProva deve retornar um http status 403 quando o usuário que tentar atualizar uma prova não for do tipo PROFESSOR")
    void atualizarProva_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaResponseDTO> provaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/{id}", HttpMethod.PUT, new HttpEntity<>(criarProvaPutRequestDTO(), headers), ProvaResponseDTO.class, 1L);
        assertThat(provaResponse).isNotNull();
        assertThat(provaResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("publicarProva deve publicar uma prova e retornar um http status 200 quando bem sucedido")
    void publicarProva_PublicaUmaProvaERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("publicarProva deve retornar um http status 403 quando o professor não existir")
    void publicarProva_Retorna403_QuandoProfessorNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        usuarioRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("publicarProva deve retornar um http status 404 quando o id da prova não existir ou ela não pertencer ao professor")
    void publicarProva_Retorna404_QuandoProvaIdNaoExistirOuNaoPertencerAoProfessor(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 2L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("publicarProva deve retornar um http status 400 quando o professor não estiver vinculado a nenhuma turma")
    void publicarProva_Retorna400_QuandoProfessorNaoEstaVinculadoANenhumaTurma(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        turmaRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("publicarProva deve retornar um http status 403 quando o usuário que tentar publicar a prova não for do tipo PROFESSOR")
    void publicarProva_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPut();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("publicarProva deve retornar um http status 400 quando a prova já estiver publicada")
    void publicarProva_Retorna400_QuandoProvaJaEstaPublicada(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPut();
        Prova prova = provaRepository.findById(1L).get();
        prova.setPublicado(true);
        prova.setTempoDeExpiracao(LocalDateTime.now().plusHours(2));
        provaRepository.save(prova);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> response
                = testRestTemplate.exchange(rootUrl + "/prova/publicar/{id}", HttpMethod.POST, new HttpEntity<>(criarPublishProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("buscarProvaAvaliada retorna uma ProvaAvaliadaResponseDTO e um http status 200 quando bem sucedido")
    void buscarProvaAvaliada_RetornaProvaAvaliadaResponseDTOEStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaAvaliadaResponseDTO> provaAvaliadaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/avaliada/{id}", HttpMethod.GET, new HttpEntity<>(headers), ProvaAvaliadaResponseDTO.class, 1L);
        assertThat(provaAvaliadaResponse).isNotNull();
        assertThat(provaAvaliadaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaAvaliadaResponse.getBody()).isNotNull();
        assertThat(provaAvaliadaResponse.getBody().getProvaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarProvaAvaliada retorna um http status 404 quando o id da prova passado não existir")
    void buscarProvaAvaliada_Retorna404_QuandoProvaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaAvaliadaResponseDTO> provaAvaliadaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/avaliada/{id}", HttpMethod.GET, new HttpEntity<>(headers), ProvaAvaliadaResponseDTO.class, 2L);
        assertThat(provaAvaliadaResponse).isNotNull();
        assertThat(provaAvaliadaResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("buscarProvaAvaliada retorna um http status 400 quando estudante não tiver feito a prova buscada")
    void buscarProvaAvaliada_Retorna400_QuandoEstudanteNaoTiverFeitoAProva(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasGet();
        respostaProvaRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaAvaliadaResponseDTO> provaAvaliadaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/avaliada/{id}", HttpMethod.GET, new HttpEntity<>(headers), ProvaAvaliadaResponseDTO.class, 1L);
        assertThat(provaAvaliadaResponse).isNotNull();
        assertThat(provaAvaliadaResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("buscarProvaAvaliada retorna um http status 403 quando o usuário que tentar buscar uma prova avaliada não for do tipo ESTUDANTE")
    void buscarProvaAvaliada_Retorna403_QuandoUsuarioNaoEEstudante(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<ProvaAvaliadaResponseDTO> provaAvaliadaResponse
                = testRestTemplate.exchange(rootUrl + "/prova/avaliada/{id}", HttpMethod.GET, new HttpEntity<>(headers), ProvaAvaliadaResponseDTO.class, 1L);
        assertThat(provaAvaliadaResponse).isNotNull();
        assertThat(provaAvaliadaResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}