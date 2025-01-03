package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.*;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TurmaControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    private String gerarTokenJWT(Usuario usuario, String password) {
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/auth/login", new LoginRequestDTO(usuarioSalvo.getEmail(), password), LoginResponseDTO.class);
        if (loginResponseDTO.getBody() != null) {
            return loginResponseDTO.getBody().getToken();
        }
        return null;
    }

    private String gerarTokenJWTERelacaoEntreEstudanteEDisciplina(Estudante estudante, String password){
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplina.setEstudantes(List.of(estudante));
        estudante.setDisciplinas(List.of(disciplina));
        Estudante estudanteSalvo = estudanteRepository.save(estudante);
        disciplinaRepository.save(disciplina);
        ResponseEntity<LoginResponseDTO> loginResponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/auth/login", new LoginRequestDTO(estudanteSalvo.getEmail(), password), LoginResponseDTO.class);
        if (loginResponseDTO.getBody() != null) {
            return loginResponseDTO.getBody().getToken();
        }
        return null;
    }

    private void adicionarDependencias(){
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
    }

    private void adicionarDependenciasGet(Usuario usuario){
        adicionarDependencias();
        usuarioRepository.save(usuario);
        Turma turma = criarTurma();
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        turma.setTurmaCode("9427396$%*");
        turma.setId(null);
        turma.setEstudantes(null);
        turma.setProfessor(null);
        turmaRepository.save(turma);
    }

    private void adicionarDependenciasGet(){
        adicionarDependencias();
        Turma turma = criarTurma();
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        turma.setTurmaCode("9427396$%*");
        turma.setId(null);
        turma.setEstudantes(null);
        turma.setProfessor(null);
        turmaRepository.save(turma);
    }

    @Test
    @DisplayName("criarTurma deve cadastrar uma turma no sistema e retornar um http status 201 quando bem sucedido")
    void criarTurma_CadastraUmaTurmaERetornaStatus201_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma", new HttpEntity<>(new CreateTurmaRequestDTO("Turma A", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("criarTurma deve retornar um http status 404 quando o id da disciplina passado não existir")
    void criarTurma_Retorna404_QuandoDisciplinaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma", new HttpEntity<>(new CreateTurmaRequestDTO("Turma A", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("criarTurma deve retornar um http status 400 quando o nome da turma passado já existir na disciplina passada")
    void criarTurma_Retorna400_QuandoNomeDaTurmaJaExistirNaDisciplina(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        adicionarDependencias();
        Turma turma = criarTurma();
        turma.setId(null);
        turmaRepository.save(turma);
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma", new HttpEntity<>(new CreateTurmaRequestDTO("Turma A", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("criarTurma deve retornar um http status 403 quando o usuário que tentar criar uma turma não for do tipo ADMIN")
    void criarTurma_Retorna403_QuandoUsuarioNaoEAdmin(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        adicionarDependencias();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma", new HttpEntity<>(new CreateTurmaRequestDTO("Turma A", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("addEstudante deve adicionar um estudante em uma turma e retornar um http status 200 quando bem sucedido")
    void addEstudante_AdicionaUmEstudanteEmUmaTurmaERetornarStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/estudante", new HttpEntity<>(new AddTurmaRequestDTO("ciclano@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("addEstudante deve retornar um http status 404 quando o e-mail do estudante passado não existir")
    void addEstudante_Retorna404_QuandoEmailDoEstudanteNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/estudante", new HttpEntity<>(new AddTurmaRequestDTO("beltrano@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("addEstudante deve retornar um http status 404 quando o id da turma passado não existir")
    void addEstudante_Retorna404_QuandoTurmaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/estudante", new HttpEntity<>(new AddTurmaRequestDTO("ciclano@example.com", 2L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("addEstudante deve retornar um http status 400 quando estudante passado já estiver cadastrado na turma passada")
    void addEstudante_Retorna400_QuandoEstudanteJaEstaCadastradoNaTurma(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        Turma turma = turmaRepository.findById(1L).get();
        Estudante estudante = criarEstudante();
        estudante.setId(2L);
        estudante.setTurmas(List.of(turma));
        turma.setEstudantes(List.of(estudante));
        turmaRepository.save(turma);
        usuarioRepository.save(estudante);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/estudante", new HttpEntity<>(new AddTurmaRequestDTO("ciclano@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("addEstudante deve retornar um http status 403 quando usuário que tentar adicionar um estudante em uma turma não for do tipo ADMIN")
    void addEstudante_Retorna403_QuandoUsuarioNaoEAdmin(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/estudante", new HttpEntity<>(new AddTurmaRequestDTO("ciclano@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("addProfessor deve adicionar um professor em uma turma e retornar um https status 200 quando bem sucedido")
    void addProfessor_AdicionaUmProfessorEmUmaTurmaERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("professor@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("addProfessor deve retornar um http status 404 quando e-mail do professor passado não existir")
    void addProfessor_Retorna404_QuandoEmailDoProfessorNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("beltrano@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("addProfessor deve retornar um http status 404 quando id da turma passado não existir")
    void addProfessor_Retorna404_QuandoTurmaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("professor@example.com", 2L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("addProfessor deve retornar um http status 400 quando professor passado já estiver cadastrado na turma passada")
    void addProfessor_Retorna400_QuandoProfessorJaEstiverCadastradoNaTurma(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        Turma turma = turmaRepository.findById(1L).get();
        Professor professor = criarProfessor();
        professor.setId(2L);
        turma.setProfessor(professor);
        turmaRepository.save(turma);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("professor@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("addProfessor deve retornar uma http status 400 quando professor já fizer parte de uma turma de outra disciplina")
    void addProfessor_Retorna400_QuandoProfessorJaEstiverEmUmaTurmaDeOutraDisciplina(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        Disciplina disciplina = disciplinaRepository.save(Disciplina.builder().name("Matemática").build());
        Professor professor = criarProfessor();
        professor.setId(2L);
        professor.setDisciplina(disciplina);
        usuarioRepository.save(professor);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("professor@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("addProfessor deve retornar um http status 403 quando usuário que tentar adicionar um professor a uma turma não for do tipo ADMIN")
    void addProfessor_Retorna403_QuandoUsuarioNaoEAdmin(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarProfessorIT());
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/professor", new HttpEntity<>(new AddTurmaRequestDTO("professor@example.com", 1L), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("gerarCodigo deve gerar um código da turma pelo id da mesma e retornar um CodeResponseDTO e um http status 200 quando bem sucedido e solicitado por um admin")
    void gerarCodigo_RetornaCodeResponseDTOEStatus200_QuandoBemSucedidoESolicitadoPorAdmin() {
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.postForEntity(rootUrl + "/turma/gerar-codigo/admin", new HttpEntity<>(new TurmaRequestDTO(1L), headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(codeResponse.getBody()).isNotNull();
        assertThat(codeResponse.getBody().getCode()).isNotNull();
    }

    @Test
    @DisplayName("gerarCodigo deve retornar um http status 404 quando o id da turma passado não existir no endpoint de admin")
    void gerarCodigo_Retorna404_QuandoTurmaIdNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependencias();
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.postForEntity(rootUrl + "/turma/gerar-codigo/admin", new HttpEntity<>(new TurmaRequestDTO(1L), headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("gerarCodigo deve retornar um http status 403 quando o usuário que tentar gerar um código de uma turma pelo endpoint de admin não for do tipo ADMIN")
    void gerarCodigo_Retorna403_QuandoUsuarioNaoEAdmin(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.postForEntity(rootUrl + "/turma/gerar-codigo/admin", new HttpEntity<>(new TurmaRequestDTO(1L), headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("gerarCodigo deve retornar um CodeResponseDTO e um http status 200 quando um professor solicitar o código de uma turma")
    void gerarCodigo_RetornaCodeResponseDTOEStatus200_QuandoProfessorSolicitarCodigoDaTurma(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        usuarioRepository.save(criarProfessor());
        Turma turma = turmaRepository.findById(1L).get();
        turma.setProfessor(criarProfessor());
        turmaRepository.save(turma);
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.exchange(rootUrl + "/turma/gerar-codigo/professor", HttpMethod.GET, new HttpEntity<>(headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(codeResponse.getBody()).isNotNull();
        assertThat(codeResponse.getBody().getCode()).isNotNull();
    }

    @Test
    @DisplayName("gerarCodigo deve retornar um http status 400 quando professor que solicitar o código da turma não estiver vinculado a nenhuma turma")
    void gerarCodigo_Retorna400_QuandoProfessorNaoEstaVinculadoANenhumaTurma(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.exchange(rootUrl + "/turma/gerar-codigo/professor", HttpMethod.GET, new HttpEntity<>(headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("gerarCodigo deve retornar um http status 403 quando o usuário que tentar solicitar o código da turma no endpoint de professor não for do tipo PROFESSOR")
    void gerarCodigo_Retorna403_QuandoUsuarioNaoEProfessor(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet(criarEstudanteIT());
        ResponseEntity<CodeResponseDTO> codeResponse
                = testRestTemplate.exchange(rootUrl + "/turma/gerar-codigo/professor", HttpMethod.GET, new HttpEntity<>(headers), CodeResponseDTO.class);
        assertThat(codeResponse).isNotNull();
        assertThat(codeResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("entrarTurma deve cadastrar um estudante em uma turma e retornar um http status 200 quando bem sucedido")
    void entrarTurma_CadastraUmEstudanteEmUmaTurmaERetornaStatus200_QuandoBemSucedido() {
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("entrarTurma deve retornar um http status 400 quando estudante já estiver vinculado a uma turma daquela disciplina")
    void entrarTurma_Retorna400_QuandoEstudanteJaEstaVinculadoAUmaTurmaDaMesmaDisciplina(){
        String tokenJWT = gerarTokenJWTERelacaoEntreEstudanteEDisciplina(criarEstudanteIT(), "ciclano");
        Turma turma = criarTurma();
        turma.setId(null);
        turma.setProfessor(null);
        turma.setEstudantes(null);
        turma.setCodeExpirationTime(LocalDateTime.now().plusHours(2));
        turma.setTurmaCode("9427396$%*");
        turmaRepository.save(turma);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("entrarTurma deve cadastrar um professor em uma turma e retornar um http status 200 quando bem sucedido")
    void entrarTurma_CadastraProfessorEmUmaTurmaERetornaStatus200_QuandoBemSucedido(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("entrarTurma deve retornar um http status 400 quando professor já estiver vinculado a turma selecionada")
    void entrarTurma_Retorna400_QuandoProfessorJaEstaVinculadoATurma(){
        String tokenJWT = gerarTokenJWT(criarProfessorIT(), "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        Professor professor = criarProfessorIT();
        professor.setId(1L);
        Turma turma = turmaRepository.findById(1L).get();
        turma.setProfessor(professor);
        turmaRepository.save(turma);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("entrarTurma deve retornar um http status 400 quando código da turma passado não existir")
    void entrarTurma_Retorna400_QuandoCodigoDaTurmaNaoExistir(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9999999999"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("entrarTurma deve retornar um http status 400 quando o código da turma passado estiver expirado")
    void entrarTurma_Retorna400_QuandoCodigoDaTurmaEstiverExpirado(){
        String tokenJWT = gerarTokenJWT(criarEstudanteIT(), "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        Turma turma = turmaRepository.findById(1L).get();
        turma.setCodeExpirationTime(LocalDateTime.now().minusHours(2));
        turmaRepository.save(turma);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("entrarTurma deve retornar um http status 403 quando o usuário que tentar entrar na turma não for do tipo PROFESSOR ou ESTUDANTE")
    void entrarTurma_Retorna403_QuandoUsuarioNaoEProfessorOuEstudante(){
        String tokenJWT = gerarTokenJWT(criarAdminIT(), "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        adicionarDependenciasGet();
        ResponseEntity<Void> responseEntity
                = testRestTemplate.postForEntity(rootUrl + "/turma/entrar", new HttpEntity<>(new CodeRequestDTO("9427396$%*"), headers), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}