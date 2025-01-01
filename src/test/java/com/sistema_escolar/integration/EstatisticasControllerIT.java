package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.response.EstatisticasEstudanteResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasGeraisResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.repositories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EstatisticasControllerIT {

    public static String rootUrl = "/api/v1";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    private String gerarTokenJWT(Usuario usuario, String password){
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTO
                = testRestTemplate.postForEntity(rootUrl + "/auth/login", new LoginRequestDTO(usuarioSalvo.getEmail(), password), LoginResponseDTO.class);
        if (loginResponseDTO.getBody() != null){
            return loginResponseDTO.getBody().getToken();
        }
        return null;
    }

    private void criarTurmaEDisciplina(){
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
        Turma turma = criarTurma();
        turma.setId(null);
        turma.setEstudantes(null);
        turmaRepository.save(turma);
    }

    @Test
    @DisplayName("estatisticasDoEstudante deve retornar EstatisticasEstudanteResponseDTO e http status 200 quando a busca por estatisticas do estudante for bem sucedida")
    void estatisticasDoEstudante_RetornaEstatisticasEstudanteResponseDTOEStatus200_QuandoABuscaPorEstatisticasEBemSucedida() {
        Estudante estudante = criarEstudante();
        estudante.setId(null);
        estudante.setIsVerified(true);
        estudante.setVerificationCode(null);
        estudante.setCodeExpirationTime(null);
        estudante.setPassword(new BCryptPasswordEncoder().encode("ciclano"));
        String tokenJWT = gerarTokenJWT(estudante, "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        ResponseEntity<EstatisticasEstudanteResponseDTO> estatisticasEstudante
                = testRestTemplate.exchange(rootUrl+"/estatisticas/estudante", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasEstudanteResponseDTO.class);
        assertThat(estatisticasEstudante).isNotNull();
        assertThat(estatisticasEstudante.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estatisticasEstudante.getBody()).isNotNull();
        assertThat(estatisticasEstudante.getBody().getMediaGeral()).isEqualTo(BigDecimal.valueOf(0D));
    }

    @Test
    @DisplayName("estatisticasDoEstudante deve retornar um http status 403 quando usuário que solicitou as estatísticas não é um estudante")
    void estatisticasDoEstudante_Retorna403_QuandoUsuarioNaoEEstudante(){
        Professor professor = criarProfessor();
        professor.setId(null);
        professor.setPassword(new BCryptPasswordEncoder().encode("professor"));
        professor.setVerificationCode(null);
        professor.setDisciplina(null);
        professor.setIsVerified(true);
        professor.setCodeExpirationTime(null);
        String tokenJWT = gerarTokenJWT(professor, "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        ResponseEntity<EstatisticasEstudanteResponseDTO> estatisticasEstudante
                = testRestTemplate.exchange(rootUrl+"/estatisticas/estudante", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasEstudanteResponseDTO.class);
        assertThat(estatisticasEstudante).isNotNull();
        assertThat(estatisticasEstudante.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(estatisticasEstudante.getBody()).isNull();
    }

    @Test
    @DisplayName("estatisticasGerais deve retornar EstatisticasGeraisResponseDTO e http status 200 quando a busca por estatísticas gerais do sistema for bem sucedida")
    void estatisticasGerais_RetornaEstatisticasGeraisResponseDTOEStatus200_QuandoABuscaPorEstatisticasGeraisEBemSucedida() {
        Disciplina disciplina = criarDisciplina();
        disciplina.setId(null);
        disciplinaRepository.save(disciplina);
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuario.setPassword(new BCryptPasswordEncoder().encode("fulano"));
        usuario.setIsVerified(true);
        usuario.setVerificationCode(null);
        usuario.setCodeExpirationTime(null);
        String tokenJWT = gerarTokenJWT(usuario, "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<EstatisticasGeraisResponseDTO> estatisticasGerais
                = testRestTemplate.exchange(rootUrl + "/estatisticas/geral", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasGeraisResponseDTO.class);
        assertThat(estatisticasGerais).isNotNull();
        assertThat(estatisticasGerais.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estatisticasGerais.getBody()).isNotNull();
        assertThat(estatisticasGerais.getBody().getQtdDisciplinasGeral()).isEqualTo(1L);
    }

    @Test
    @DisplayName("estatisticasGerais deve retornar um http status 403 quando o usuário que solicitou as estatísticas não for do tipo ADMIN")
    void estatisticasGerais_Retorna403_QuandoUsuarioNaoEAdmin(){
        Estudante estudante = criarEstudante();
        estudante.setId(null);
        estudante.setVerificationCode(null);
        estudante.setCodeExpirationTime(null);
        estudante.setIsVerified(true);
        estudante.setPassword(new BCryptPasswordEncoder().encode("ciclano"));
        String tokenJWT = gerarTokenJWT(estudante, "ciclano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<EstatisticasGeraisResponseDTO> estatisticasGerais
                = testRestTemplate.exchange(rootUrl + "/estatisticas/geral", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasGeraisResponseDTO.class);
        assertThat(estatisticasGerais).isNotNull();
        assertThat(estatisticasGerais.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(estatisticasGerais.getBody()).isNull();
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar EstatisticasTurmaResponseDTO e um http status 200 quando a busca por estatísticas da turma for bem sucedida")
    void estatisticasDaTurma_RetornaEstatisticasTurmaResponseDTOEStatus200_QuandoABuscaPorEstatisticasDaTurmaEBemSucedida() {
        Professor professor = criarProfessor();
        professor.setId(null);
        professor.setPassword(new BCryptPasswordEncoder().encode("professor"));
        professor.setVerificationCode(null);
        professor.setDisciplina(null);
        professor.setIsVerified(true);
        professor.setCodeExpirationTime(null);
        String tokenJWT = gerarTokenJWT(professor, "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        criarTurmaEDisciplina();
        ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasTurma
                = testRestTemplate.exchange(rootUrl + "/estatisticas/turma/{id}", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasTurmaResponseDTO.class, 1);
        assertThat(estatisticasTurma).isNotNull();
        assertThat(estatisticasTurma.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estatisticasTurma.getBody()).isNotNull();
        assertThat(estatisticasTurma.getBody().getPorcentagemAprovados()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar um http status 404 quando o professor que solicitou as estatísticas não fizer parte da turma")
    void estatisticasDaTurma_Retorna404_QuandoProfessorNaoFizerParteDaTurma(){
        Professor professor = criarProfessor();
        professor.setId(null);
        professor.setPassword(new BCryptPasswordEncoder().encode("professor"));
        professor.setVerificationCode(null);
        professor.setDisciplina(null);
        professor.setIsVerified(true);
        professor.setCodeExpirationTime(null);
        String tokenJWT = gerarTokenJWT(professor, "professor");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ tokenJWT);
        ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasTurma
                = testRestTemplate.exchange(rootUrl + "/estatisticas/turma/{id}", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasTurmaResponseDTO.class, 1);
        assertThat(estatisticasTurma).isNotNull();
        assertThat(estatisticasTurma.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar um http status 403 quando o usuário que solicitou as estatísticas não for do tipo PROFESSOR")
    void estatisticasDaTurma_Retorna403_QuandoUsuarioNaoEProfessor(){
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuario.setPassword(new BCryptPasswordEncoder().encode("fulano"));
        usuario.setIsVerified(true);
        usuario.setVerificationCode(null);
        usuario.setCodeExpirationTime(null);
        String tokenJWT = gerarTokenJWT(usuario, "fulano");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+tokenJWT);
        ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasTurma
                = testRestTemplate.exchange(rootUrl + "/estatisticas/turma/{id}", HttpMethod.GET, new HttpEntity<>(headers), EstatisticasTurmaResponseDTO.class, 1);
        assertThat(estatisticasTurma).isNotNull();
        assertThat(estatisticasTurma.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}