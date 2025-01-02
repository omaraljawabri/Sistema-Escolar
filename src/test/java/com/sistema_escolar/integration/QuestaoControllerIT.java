package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.entities.Turma;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
import com.sistema_escolar.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QuestaoControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

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
    }

    @Test
    @DisplayName("buscarQuestoes deve retornar uma lista paginada de QuestaoResponseDTO e um http status 200 quando bem sucedido")
    void buscarQuestoes_RetornaListaPaginaDeQuestaoResponseDTOEStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        adicionarDependencias();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<PageableResponse<QuestaoResponseDTO>> questaoResponse = testRestTemplate.exchange(rootUrl + "/questao?pagina=0&quantidade=1", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PageableResponse<QuestaoResponseDTO>>() {
        });
        assertThat(questaoResponse).isNotNull();
        assertThat(questaoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(questaoResponse.getBody()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(questaoResponse.getBody().getContent().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarQuestoes deve retornar um http status 403 quando o usuário que tentar buscar questões não for do tipo PROFESSOR")
    void buscarQuestoes_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        adicionarDependencias();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<PageableResponse<QuestaoResponseDTO>> questaoResponse = testRestTemplate.exchange(rootUrl + "/questao?pagina=0&quantidade=1", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<PageableResponse<QuestaoResponseDTO>>() {
        });
        assertThat(questaoResponse).isNotNull();
        assertThat(questaoResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}