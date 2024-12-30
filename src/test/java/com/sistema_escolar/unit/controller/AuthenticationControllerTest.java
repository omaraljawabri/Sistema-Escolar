package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.AuthenticationController;
import com.sistema_escolar.dtos.request.ChangePasswordEmailRequestDTO;
import com.sistema_escolar.dtos.request.ChangePasswordRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.AccountWasntValidatedException;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.exceptions.InvalidCodeException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.infra.security.TokenService;
import com.sistema_escolar.services.AuthenticationService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup(){
        doNothing().when(authenticationService).registrarUsuario(ArgumentMatchers.any(RegisterRequestDTO.class));
        when(authenticationService.login(ArgumentMatchers.any(LoginRequestDTO.class), ArgumentMatchers.anyString()))
                .thenReturn(criarLoginResponseDTO());
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication());
        when(tokenService.generateToken(ArgumentMatchers.any(Usuario.class)))
                .thenReturn("mocked-token");
        doNothing().when(authenticationService).verificarCodigo(ArgumentMatchers.anyString());
        doNothing().when(authenticationService).mudarSenha(ArgumentMatchers.any(ChangePasswordEmailRequestDTO.class));
        doNothing().when(authenticationService).verificarMudarSenha(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChangePasswordRequestDTO.class));
    }

    private Authentication mockAuthentication() {
        Usuario usuario = new Usuario();
        usuario.setEmail("fulano@gmail.com");
        usuario.setPassword("fulano");
        return new UsernamePasswordAuthenticationToken(usuario, null);
    }

    @Test
    @DisplayName("registrar deve cadastrar um usuário no sistema quando a operação for bem sucedida")
    void registrar_CadastrarUmUsuarioNoSistema_QuandoBemSucedido() {
        assertThatCode(() -> authenticationController.registrar(criarRegisterRequestDTO()))
                .doesNotThrowAnyException();

        ResponseEntity<Void> registrar = authenticationController.registrar(criarRegisterRequestDTO());
        assertThat(registrar).isNotNull();
        assertThat(registrar.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("registrar deve lançar uma EntityAlreadyExistsException quando o email do usuário a ser registrado já existir")
    void registrar_LancaEntityAlreadyExistsException_QuandoEmailDoUsuarioExistir(){
        doThrow(new EntityAlreadyExistsException("Email fulano@gmail.com já existe"))
                .when(authenticationService).registrarUsuario(ArgumentMatchers.any(RegisterRequestDTO.class));
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> authenticationController.registrar(criarRegisterRequestDTO()))
                .withMessage("Email fulano@gmail.com já existe");
    }

    @Test
    @DisplayName("login deve retornar uma LoginResponseDTO quando o usuário for logado com sucesso")
    void login_RetornaLoginResponseDTO_QuandoUsuarioELogadoComSucesso() {
        assertThatCode(() -> authenticationController.login(criarLoginRequestDTO()))
                .doesNotThrowAnyException();
        ResponseEntity<LoginResponseDTO> login = authenticationController.login(criarLoginRequestDTO());
        assertThat(login.getBody()).isNotNull();
        assertThat(login.getBody().getEmail()).isEqualTo("fulano@gmail.com");
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("login deve lançar uma UserNotFoundException quando o email do usuário não existir no sistema")
    void login_LancaUserNotFoundException_QuandoEmailDoUsuarioNaoExistir(){
        doThrow(new UserNotFoundException("Email enviado não existe!"))
                .when(authenticationService).login(ArgumentMatchers.any(LoginRequestDTO.class), ArgumentMatchers.anyString());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> authenticationController.login(criarLoginRequestDTO()))
                .withMessage("Email enviado não existe!");
    }

    @Test
    @DisplayName("login deve lançar uma AccountWasntValidatedException quando a conta do usuário não estiver validada")
    void login_LancaAccountWasntValidatedException_QuandoContaDoUsuarioNaoEstiverValidada(){
        doThrow(new AccountWasntValidatedException("Conta não foi validada!"))
                .when(authenticationService).login(ArgumentMatchers.any(LoginRequestDTO.class), ArgumentMatchers.anyString());
        assertThatExceptionOfType(AccountWasntValidatedException.class)
                .isThrownBy(() -> authenticationController.login(criarLoginRequestDTO()))
                .withMessage("Conta não foi validada!");
    }

    @Test
    @DisplayName("verificarCodigo deve validar o código enviado pelo usuário quando bem sucedido")
    void verificarCodigo_ValidaOCodigoDoUsuario_QuandoBemSucedido() {
        assertThatCode(() -> authenticationController.verificarCodigo("mocked-code"))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = authenticationController.verificarCodigo("mocked-code");
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("verificarCodigo deve lançar uma InvalidCodeException quando o código enviado não for válido")
    void verificarCodigo_LancaInvalidCodeException_QuandoCodigoEnviadoForInvalido(){
        doThrow(new InvalidCodeException("Código de verificação inválido"))
                .when(authenticationService).verificarCodigo(ArgumentMatchers.anyString());
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationController.verificarCodigo("invalidmocked-code"))
                .withMessage("Código de verificação inválido");
    }

    @Test
    @DisplayName("pedirMudancaDeSenha deve solicitar a mudança de senha do usuário quando bem sucedido")
    void pedirMudancaDeSenha_SolicitaAMudancaDaSenhaDoUsuario_QuandoBemSucedido() {
        assertThatCode(() -> authenticationController.pedirMudancaDeSenha(new ChangePasswordEmailRequestDTO("fulano@gmail.com")))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = authenticationController.pedirMudancaDeSenha(new ChangePasswordEmailRequestDTO("fulano@gmail.com"));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("pedirMudancaDeSenha deve lançar uma UserNotFoundException quando o email do usuário não existir")
    void pedirMudancaDeSenha_LancaUserNotFoundException_QuandoEmailDoUsuarioNaoExistir(){
        doThrow(new UserNotFoundException("Email enviado não está cadastrado"))
                .when(authenticationService).mudarSenha(ArgumentMatchers.any(ChangePasswordEmailRequestDTO.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> authenticationController.pedirMudancaDeSenha(new ChangePasswordEmailRequestDTO("ciclano@gmail.com")))
                .withMessage("Email enviado não está cadastrado");
    }

    @Test
    @DisplayName("verificarMudancaDeSenha verifica o pedido de mudança de senha do usuário e altera a senha do mesmo quando bem sucedido")
    void verificarMudancaDeSenha_VerificaOPedidoDeMudancaDeSenhaEAlteraSenhaDoUsuario_QuandoBemSucedido() {
        assertThatCode(() -> authenticationController.verificarMudancaDeSenha("mocked-code", new ChangePasswordRequestDTO("fulano10")))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = authenticationController.verificarMudancaDeSenha("mocked-code", new ChangePasswordRequestDTO("fulano10"));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("verificarMudancaDeSenha deve lançar uma InvalidCodeException quando houver algum erro na mudança de senha do usuário")
    void verificarMudancaDeSenha_LancaInvalidCodeException_QuandoHouverErroNaMudancaDeSenha(){
        doThrow(new InvalidCodeException("Código de validação inválido!"))
                .when(authenticationService).verificarMudarSenha(ArgumentMatchers.anyString(), ArgumentMatchers.any(ChangePasswordRequestDTO.class));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationController.verificarMudancaDeSenha("invalidmocked-code", new ChangePasswordRequestDTO("fulano10")))
                .withMessage("Código de validação inválido!");
    }
}