package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.CriarDisciplinaRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
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

import static com.sistema_escolar.utils.EntityUtils.criarAdminIT;
import static com.sistema_escolar.utils.EntityUtils.criarProfessorIT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DisciplinaControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    private String gerarTokenJWT(Usuario usuario, String password){
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/auth/login", new LoginRequestDTO(usuarioSalvo.getEmail(), password), LoginResponseDTO.class);
        if (loginResponseDTO.getBody() != null){
            return loginResponseDTO.getBody().getToken();
        }
        return null;
    }

    @Test
    @DisplayName("criarDisciplina deve retornar um http status 201 quando uma disciplina for criada com sucesso")
    void criarDisciplina_Retorna201_QuandoDisciplinaECriadaComSucesso() {
        Usuario usuario = criarAdminIT();
        String tokenJWT = gerarTokenJWT(usuario, "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(rootUrl + "/disciplina", new HttpEntity<>(new CriarDisciplinaRequestDTO("Geografia"), headers),Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("criarDisciplina deve retornar um http status 400 quando o nome da disciplina a ser criada já existir")
    void criarDisciplina_Retorna400_QuandoNomeDaDisciplinaJaExistir(){
        disciplinaRepository.save(Disciplina.builder().nome("Geografia").build());
        Usuario usuario = criarAdminIT();
        String tokenJWT = gerarTokenJWT(usuario, "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(rootUrl + "/disciplina", new HttpEntity<>(new CriarDisciplinaRequestDTO("Geografia"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("criarDisciplina deve retornar um http status 403 quando o usuário que tentar criar a disciplia não for do tipo ADMIN")
    void criarDisciplina_Retorna403_QuandoUsuarioNaoEAdmin(){
        Professor professor = criarProfessorIT();
        String tokenJWT = gerarTokenJWT(professor, "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(rootUrl + "/disciplina", new HttpEntity<>(new CriarDisciplinaRequestDTO("Geografia"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.getBody()).isNull();
    }
}