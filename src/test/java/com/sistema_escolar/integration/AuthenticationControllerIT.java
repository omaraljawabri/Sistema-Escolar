package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.request.MudarSenhaEmailRequestDTO;
import com.sistema_escolar.dtos.request.MudarSenhaRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.RedefinirSenha;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.repositories.RedefinirSenhaRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationControllerIT {

    public static String rootUrl = "/api/v1/auth";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RedefinirSenhaRepository redefinirSenhaRepository;

    @Test
    @DisplayName("registrar deve cadastrar um usuário no sistema e retornar http status 200 quando bem sucedido")
    void registrar_CadastraUmUsuarioNoSistemaERetorna200_QuandoBemSucedido() {
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/registrar", HttpMethod.POST, new HttpEntity<>(criarEstudante()), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("registrar deve retornar 400 quando o usuário a ser registrado já existir no banco de dados")
    void registrar_Retorna400_QuandoUsuarioASerRegistradoJaExistirNoBancoDeDados(){
        Estudante estudante = criarEstudante();
        estudante.setId(null);
        usuarioRepository.save(estudante);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/registrar", HttpMethod.POST, new HttpEntity<>(criarEstudante()), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("login deve retornar LoginResponseDTO e http status 200 quando a operação for bem sucedida")
    void login_RetornaLoginResponseDTOEStatus200_QuandoBemSucedido() {
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuario.setVerificado(true);
        usuario.setCodigoDeVerificacao(null);
        usuario.setSenha(new BCryptPasswordEncoder().encode("fulano"));
        usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTOResponseEntity
                = testRestTemplate.postForEntity(rootUrl + "/login", criarLoginRequestDTO(), LoginResponseDTO.class);
        assertThat(loginResponseDTOResponseEntity).isNotNull();
        assertThat(loginResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(loginResponseDTOResponseEntity.getBody().getEmail()).isEqualTo("fulano@example.com");
    }

    @Test
    @DisplayName("login deve retornar http status 403 quando o usuário a ser logado não existir no sistema")
    void login_Retorna403_QuandoUsuarioASerLogadoNaoExistir(){
        ResponseEntity<LoginResponseDTO> loginResponseDTOResponseEntity
                = testRestTemplate.postForEntity(rootUrl + "/login", criarLoginRequestDTO(), LoginResponseDTO.class);
        assertThat(loginResponseDTOResponseEntity).isNotNull();
        assertThat(loginResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(loginResponseDTOResponseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("login deve retornar http status 400 quando a conta do usuário a ser logado não tiver sido verificada")
    void login_Retorna400_QuandoContaDoUsuarioNaoTiverSidoVerificada(){
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuario.setSenha(new BCryptPasswordEncoder().encode("fulano"));
        usuarioRepository.save(usuario);
        ResponseEntity<LoginResponseDTO> loginResponseDTOResponseEntity
                = testRestTemplate.postForEntity(rootUrl + "/login", criarLoginRequestDTO(), LoginResponseDTO.class);
        assertThat(loginResponseDTOResponseEntity).isNotNull();
        assertThat(loginResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("verificarCodigo deve validar o código do usuário e retornar http status 200 quando bem sucedido")
    void verificarCodigo_ValidaCodigoDoUsuarioERetorna200_QuandoBemSucedido() {
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuarioRepository.save(usuario);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/verificar?code=acde070d-8c4c-4f0d-9d8a-162843c10333", HttpMethod.GET, null, Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("verificarCodigo deve retornar um http status 400 quando o código passado for inválido")
    void verificarCodigo_Retorna400_QuandoCodigoEInvalido(){
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuarioRepository.save(usuario);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/verificar?code=invalido", HttpMethod.GET, null, Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("pedirMudancaDeSenha deve retornar um http status 200 quando a requisição de mudança de senha for bem sucedida")
    void pedirMudancaDeSenha_Retorna200_QuandoARequisicaoParaMudancaDeSenhaForBemSucedida() {
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuarioRepository.save(usuario);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/mudar-senha/requisicao", HttpMethod.POST, new HttpEntity<>(new MudarSenhaEmailRequestDTO("fulano@example.com")), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("pedirMudancaDeSenha deve retornar um http status 404 quando o email do usuário que solicitou a mudança não existir")
    void pedirMudancaDeSenha_Retorna404_QuandoEmailDoUsuarioNaoExistir(){
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/mudar-senha/requisicao", HttpMethod.POST, new HttpEntity<>(new MudarSenhaEmailRequestDTO("ciclano@example.com")), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("verificarMudancaDeSenha deve retornar um http status 200 quando a senha for mudada com sucesso")
    void verificarMudancaDeSenha_Retorna200_QuandoSenhaEMudadaComSucesso() {
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuarioRepository.save(usuario);
        RedefinirSenha redefinirSenha = criarRedefinirSenha();
        redefinirSenha.setId(null);
        redefinirSenhaRepository.save(redefinirSenha);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/mudar-senha/verificar?code=acde070d-8c4c-4f0d-9d8a-162843c10334", HttpMethod.POST, new HttpEntity<>(new MudarSenhaRequestDTO("fulano10")), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("verificarMudancaDeSenha deve retornar um http status 400 quando o código de verificação for inválido")
    void verificarMudancaDeSenha_Retorna400_QuandoCodigoDeVerificacaoForInvalido(){
        Usuario usuario = criarUsuario();
        usuario.setId(null);
        usuarioRepository.save(usuario);
        RedefinirSenha redefinirSenha = criarRedefinirSenha();
        redefinirSenha.setId(null);
        redefinirSenha.setTempoDeExpiracaoCodigo(LocalDateTime.now().minusHours(2));
        redefinirSenhaRepository.save(redefinirSenha);
        ResponseEntity<Void> responseEntity
                = testRestTemplate.exchange(rootUrl + "/mudar-senha/verificar?code=acde070d-8c4c-4f0d-9d8a-162843c10334", HttpMethod.POST, new HttpEntity<>(new MudarSenhaRequestDTO("fulano10")), Void.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}