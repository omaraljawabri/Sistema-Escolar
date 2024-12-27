package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.ChangePasswordEmailRequestDTO;
import com.sistema_escolar.dtos.request.ChangePasswordRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.RedefinirSenha;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.AccountWasntValidatedException;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.exceptions.InvalidCodeException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.*;
import com.sistema_escolar.services.AuthenticationService;
import com.sistema_escolar.services.MailService;
import com.sistema_escolar.utils.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RedefinirSenhaRepository redefinirSenhaRepository;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setup(){
        when(usuarioRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarUsuario()));
        when(usuarioRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarUsuario()));
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of((criarUsuario())));
        when(redefinirSenhaRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarRedefinirSenha()));
        doNothing().when(mailService).enviarEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("registrarUsuario deve cadastrar um usuário do tipo Admin no sistema quando a operação for bem sucedida")
    void registrarUsuario_CadastraUsuarioDoTipoAdminNoSistema_QuandoBemSucedido() {
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatCode(() -> authenticationService.registrarUsuario(criarRegisterRequestDTO()))
                .doesNotThrowAnyException();
        String subject = "Validação de cadastro";
        verify(mailService, times(1))
                .enviarEmail(Mockito.eq(criarRegisterRequestDTO().getEmail()),
                        Mockito.eq(subject), Mockito.contains("http://localhost:8080/api/v1/auth/verify?code="));
    }

    @Test
    @DisplayName("registrarUsuario deve cadastrar um usuário do tipo Professor no sistema quando a operação for bem sucedida")
    void registrarUsuario_CadastraUsuarioDoTipoProfessorNoSistema_QuandoBemSucedido() {
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        RegisterRequestDTO registerRequestDTO = criarRegisterRequestDTO();
        registerRequestDTO.setRole(UserRole.PROFESSOR);
        assertThatCode(() -> authenticationService.registrarUsuario(registerRequestDTO))
                .doesNotThrowAnyException();
        String subject = "Validação de cadastro";
        verify(mailService, times(1))
                .enviarEmail(Mockito.eq(criarRegisterRequestDTO().getEmail()),
                        Mockito.eq(subject), Mockito.contains("http://localhost:8080/api/v1/auth/verify?code="));
    }

    @Test
    @DisplayName("registrarUsuario deve cadastrar um usuário do tipo Estudante no sistema quando a operação for bem sucedida")
    void registrarUsuario_CadastraUsuarioDoTipoEstudanteNoSistema_QuandoBemSucedido() {
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        RegisterRequestDTO registerRequestDTO = criarRegisterRequestDTO();
        registerRequestDTO.setRole(UserRole.ESTUDANTE);
        assertThatCode(() -> authenticationService.registrarUsuario(registerRequestDTO))
                .doesNotThrowAnyException();
        String subject = "Validação de cadastro";
        verify(mailService, times(1))
                .enviarEmail(Mockito.eq(criarRegisterRequestDTO().getEmail()),
                        Mockito.eq(subject), Mockito.contains("http://localhost:8080/api/v1/auth/verify?code="));
    }

    @Test
    @DisplayName("registrarUsuario deve lançar uma EntityAlreadyExistsException quando o e-mail já exisitr")
    void registrarUsuario_LancaEntityAlreadyExistsException_QuandoEmailEnviadoJaExistir(){
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarUsuario()));
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> authenticationService.registrarUsuario(criarRegisterRequestDTO()))
                .withMessage("Email "+criarUsuario().getEmail()+ " já existe");
        verify(mailService, times(0))
                .enviarEmail(Mockito.eq(criarRegisterRequestDTO().getEmail()),
                        Mockito.eq("Validação de cadastro"), Mockito.contains("http://localhost:8080/api/v1/auth/verify?code="));
    }

    @Test
    @DisplayName("verificarCodigo deve validar o usuário a partir do código passado quando bem sucedido")
    void verificarCodigo_ValidaUsuario_QuandoBemSucedido() {
        assertThatCode(() -> authenticationService.verificarCodigo("acde070d-8c4c-4f0d-9d8a-162843c10333"))
                .doesNotThrowAnyException();
        Usuario usuario = criarUsuario();
        usuario.setIsVerified(true);
        usuario.setVerificationCode(null);
        usuario.setCodeExpirationTime(null);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("verificarCodigo deve lançar uma InvalidCodeException quando o código enviado não for encontrado no banco de dados")
    void verificarCodigo_LancaInvalidCodeException_QuandoCodigoEnviadoNaoExistir(){
        when(usuarioRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationService.verificarCodigo("42937659"))
                .withMessage("Código de verificação inválido");
    }

    @Test
    @DisplayName("verificarCodigo deve lançar uma InvalidCodeException quando o código enviado já estiver expirado")
    void verificarCodigo_LancaInvalidCodeException_QuandoCodigoEnviadoEstiverExpirado(){
        Usuario usuario = criarUsuario();
        usuario.setCodeExpirationTime(LocalDateTime.now().minusHours(2));
        when(usuarioRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(usuario));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationService.verificarCodigo("acde070d-8c4c-4f0d-9d8a-162843c10333"))
                .withMessage("Código de verificação inválido");
    }

    @Test
    @DisplayName("login deve retornar um LoginResponseDTO quando o login do usuário for bem sucedido")
    void login_RetornaLoginResponseDTO_QuandoLoginEBemSucedido() {
        Usuario usuario = criarUsuario();
        usuario.setIsVerified(true);
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(usuario));
        LoginResponseDTO loginResponse = authenticationService.login(criarLoginRequestDTO(), "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getEmail()).isEqualTo("fulano@gmail.com");
    }

    @Test
    @DisplayName("login deve lançar uma UserNotFoundException quando o e-mail do usuário não estiver cadastrado no banco de dados")
    void login_LancaUserNotFoundException_QuandoEmailNaoExistir(){
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> authenticationService.login(criarLoginRequestDTO(), "5497357754457"))
                .withMessage("Email enviado não existe!");
    }

    @Test
    @DisplayName("login deve lançar uma AccountWasntValidatedException quando a conta do usuário não estiver validada")
    void login_LancaAccountWasntValidatedException_QuandoContaNaoEstiverValidada(){
        Usuario usuario = criarUsuario();
        usuario.setIsVerified(false);
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(usuario));
        assertThatExceptionOfType(AccountWasntValidatedException.class)
                .isThrownBy(() -> authenticationService.login(criarLoginRequestDTO(), "05643874605736"))
                .withMessage("Conta não foi validada!");
    }

    @Test
    @DisplayName("mudarSenha deve iniciar o processo de mudança de senha e enviar o e-mail ao usuário quando bem sucedido")
    void mudarSenha_IniciaProcessoDeMudancaDeSenha_QuandoBemSucedido() {
        assertThatCode(() -> authenticationService.mudarSenha(new ChangePasswordEmailRequestDTO("fulano@gmail.com")))
                .doesNotThrowAnyException();
        verify(mailService, times(1)).enviarEmail(Mockito.eq("fulano@gmail.com"),
                Mockito.eq("Redefinição de senha"), Mockito.contains("http://localhost:8080/api/v1/auth/change-password/verify?code="));
    }

    @Test
    @DisplayName("mudarSenha deve lançar uma UserNotFoundException quando o e-mail do usuário não estiver cadastrado no banco de dados")
    void mudarSenha_LancaUserNotFoundException_QuandoEmailNaoExistir(){
        when(usuarioRepository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> authenticationService.mudarSenha(new ChangePasswordEmailRequestDTO("ciclano@gmail.com")))
                .withMessage("Email enviado não está cadastrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("fulano@gmail.com"),
                Mockito.eq("Redefinição de senha"), Mockito.contains("http://localhost:8080/api/v1/auth/change-password/verify?code="));
    }

    @Test
    @DisplayName("verificarMudarSenha verifica o código de validação enviado e altera a senha do usuário quando bem sucedido")
    void verificarMudarSenha_VerificaCodigoEAlteraSenha_QuandoBemSucedido() {
        assertThatCode(() -> authenticationService.verificarMudarSenha("acde070d-8c4c-4f0d-9d8a-162843c10334",
                new ChangePasswordRequestDTO("fulano10"))).doesNotThrowAnyException();
        verify(redefinirSenhaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("verificarMudarSenha lança uma InvalidCodeException quando o código enviado não existir no banco de dados")
    void verificarMudarSenha_LancaInvalidCodeException_QuandoCodigoNaoExistir(){
        when(redefinirSenhaRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationService.verificarMudarSenha("64397860", new ChangePasswordRequestDTO("fulano10")));
        verify(usuarioRepository, times(0)).save(criarUsuario());
        verify(redefinirSenhaRepository, times(0)).deleteById(1L);
    }

    @Test
    @DisplayName("verificarMudarSenha lança uma InvalidCodeException quando o código enviado já estiver expirado")
    void verificarMudarSenha_LancaInvalidCodeException_QuandoCodigoEstiverExpirado(){
        RedefinirSenha redefinirSenha = criarRedefinirSenha();
        redefinirSenha.setExpirationCodeTime(LocalDateTime.now().minusHours(2));
        when(redefinirSenhaRepository.findByVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(redefinirSenha));
        assertThatExceptionOfType(InvalidCodeException.class)
                .isThrownBy(() -> authenticationService.verificarMudarSenha("acde070d-8c4c-4f0d-9d8a-162843c10334",
                        new ChangePasswordRequestDTO("fulano10")));
        verify(usuarioRepository, times(0)).save(criarUsuario());
        verify(redefinirSenhaRepository, times(0)).deleteById(1L);
    }
}