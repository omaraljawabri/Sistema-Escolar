package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.dtos.response.ProvaRespondidaResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RespostaProvaControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

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

    private void adicionarDependencias(){
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
        Turma turma = criarTurma();
        turma.setId(null);
        turma.setEstudantes(null);
        turmaRepository.save(turma);
        Questao questao = criarQuestao();
        questao.setId(null);
        questaoRepository.save(questao);
        Prova prova = criarProva();
        prova.setId(null);
        provaRepository.save(prova);
    }

    private void adicionarDependenciasPost(){
        adicionarDependencias();
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setId(null);
        respostaProva.setRespondida(false);
        respostaProvaRepository.save(respostaProva);
    }

    private void adicionarDependenciasGet(){
        Estudante estudante = criarEstudanteIT();
        Estudante estudanteSalvo = usuarioRepository.save(estudante);
        adicionarDependenciasPost();
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setId(null);
        respostaProva.setEstudante(estudanteSalvo);
        respostaProvaRepository.save(respostaProva);
    }


    @Test
    @DisplayName("responderProva deve cadastrar uma resposta do estudante e retornar um http status 200 quando bem sucedido")
    void responderProva_CadastraUmaRespostaDoEstudanteERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("responderProva retorna um http status 403 quando o id do estudante não existir")
    void responderProva_Retorna403_QuandoEstudanteIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        usuarioRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("responderProva retorna um http status 404 quando id da prova não existir")
    void responderProva_Retorna404_QuandoProvaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 2L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("responderProva retorna um http status 400 quando a prova ainda não tiver sido publicada ou já estiver expirado")
    void responderProva_Retorna400_QuandoProvaAindaNaoFoiPublicadaOuEstaExpirada(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPost();
        Prova prova = provaRepository.findById(1L).get();
        prova.setExpirationTime(LocalDateTime.now().minusHours(2));
        provaRepository.save(prova);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("responderProva retorna um http status 400 quando estudante já estiver respondido a prova")
    void responderProva_Retorna400_QuandoEstudanteJaTiverRespondidoAProva(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasPost();
        RespostaProva respostaProva = respostaProvaRepository.findById(1L).get();
        respostaProva.setRespondida(true);
        respostaProvaRepository.save(respostaProva);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("responderProva retorna um http status 403 quando o usuário que tentar responder a prova não for do tipo ESTUDANTE")
    void responderProva_Retorna403_QuandoUsuarioNaoEEstudante(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/resposta-prova/{id}", new HttpEntity<>(criarRespostaProvaRequestDTO(), headers), Void.class, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista de ProvaRespondidaResponseDTO e um http status 200 quando a busca é bem sucedida")
    void provasRespondidas_RetornaListaDeProvaRespondidaResponseDTOEStatus200_QuandoBuscaEBemSucedida() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<List<ProvaRespondidaResponseDTO>> responseEntity
                = testRestTemplate.exchange(rootUrl + "/resposta-prova/{provaId}", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProvaRespondidaResponseDTO>>(){}, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(responseEntity.getBody().getFirst().getEstudanteId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista vazia e um http status 200 quando não houverem resultados")
    void provasRespondidas_RetornaListaVaziaEStatus200_QuandoNaoHouveremResultados(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasPost();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<List<ProvaRespondidaResponseDTO>> responseEntity
                = testRestTemplate.exchange(rootUrl + "/resposta-prova/{provaId}", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProvaRespondidaResponseDTO>>(){}, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("provasRespondidas retorna um http statuxs 403 quando o id do professor não existir")
    void provasRespondidas_Retorna403_QuandoProfessorIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        usuarioRepository.deleteById(1L);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<List<ProvaRespondidaResponseDTO>> responseEntity
                = testRestTemplate.exchange(rootUrl + "/resposta-prova/{provaId}", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProvaRespondidaResponseDTO>>(){}, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("provasRespondidas retorna um http status 400 quando o id da prova passado não pertencer ao professor que fez a busca")
    void provasRespondidas_Retorna400_QuandoProvaNaoPertenceAoProfessor(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/resposta-prova/{provaId}", HttpMethod.GET, new HttpEntity<>(headers), Void.class, 2L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("provasRespondidas retorna um http status 403 quando o usuário que buscar por provas respondidas não for do tipo PROFESSOR")
    void provasRespondidas_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependenciasGet();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<List<ProvaRespondidaResponseDTO>> responseEntity
                = testRestTemplate.exchange(rootUrl + "/resposta-prova/{provaId}", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProvaRespondidaResponseDTO>>(){}, 1L);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}