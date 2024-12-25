package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.ChangePasswordEmailRequestDTO;
import com.sistema_escolar.dtos.request.ChangePasswordRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.utils.enums.UserRole;
import com.sistema_escolar.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final RedefinirSenhaRepository redefinirSenhaRepository;
    private final MailService mailService;

    @Transactional
    public void registerUser(RegisterRequestDTO registerRequestDTO){
        if (usuarioRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("Email enviado já existe"); //Do custom exception
        }
        String verificationCode = UUID.randomUUID().toString();
        LocalDateTime codeExpirationTime = LocalDateTime.now().plusHours(24);
        String password = new BCryptPasswordEncoder().encode(registerRequestDTO.getPassword());
        if (registerRequestDTO.getRole() == UserRole.ADMIN){
            Admin admin
                    = new Admin(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registerRequestDTO.getFirstName(), registerRequestDTO.getLastName());
            adminRepository.save(admin);
        } else if (registerRequestDTO.getRole() == UserRole.PROFESSOR){
            Professor professor
                    = new Professor(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registerRequestDTO.getFirstName(), registerRequestDTO.getLastName());
            professorRepository.save(professor);
        } else{
            Estudante estudante
                    = new Estudante(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registerRequestDTO.getFirstName(), registerRequestDTO.getLastName());
            estudanteRepository.save(estudante);
        }
        String verificationLink = String.format("http://localhost:8080/api/v1/auth/verify?code=%s", verificationCode);
        String subject = "Validação de cadastro";
        String textMessage = String.format("Olá, recebemos uma solicitação de cadastro na nossa plataforma utilizando este e-mail. %nCaso deseje validar sua conta em nossa plataforma, clique no link abaixo: %n%s", verificationLink);
        mailService.sendEmail(registerRequestDTO.getEmail(), subject, textMessage);
    }

    public void verifyCode(String code){
        Optional<Usuario> usuario = usuarioRepository.findByVerificationCode(code);
        if (usuario.isEmpty() || usuario.orElseThrow().getCodeExpirationTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Código de verificação inválido"); //Make customized exception
        }
        usuario.orElseThrow().setIsVerified(true);
        usuario.orElseThrow().setVerificationCode(null);
        usuario.orElseThrow().setCodeExpirationTime(null);
        usuarioRepository.save(usuario.get());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, String token){
        Usuario usuario
                = usuarioRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(() -> new RuntimeException("Email enviado não existe!"));
        if (Boolean.FALSE.equals(usuario.getIsVerified())){
            throw new RuntimeException("Conta não foi validada!");
        }
        return LoginResponseDTO.builder().email(loginRequestDTO.getEmail()).token(token).build();
    }

    @Transactional
    public void changePassword(ChangePasswordEmailRequestDTO changePasswordEmailRequestDTO){
        Usuario usuario
                = usuarioRepository.findByEmail(changePasswordEmailRequestDTO.getEmail()).orElseThrow(() -> new RuntimeException("Email enviado não está cadastrado"));
        String verificationCode = UUID.randomUUID().toString();
        RedefinirSenha redefinirSenha = RedefinirSenha.builder().verificationCode(verificationCode).expirationCodeTime(LocalDateTime.now().plusHours(24))
                .usuario(usuario).build();
        redefinirSenhaRepository.save(redefinirSenha);
        String verificationLink = String.format("http://localhost:8080/api/v1/auth/change-password/verify?code=%s",verificationCode);
        String subject = "Redefinição de senha";
        String textMessage = String.format("Olá, recebemos seu pedido para redefinição de senha!%nClique no link abaixo para prosseguir com o processo!%n%s",verificationLink);
        mailService.sendEmail(changePasswordEmailRequestDTO.getEmail(), subject, textMessage);
    }

    @Transactional
    public void verifyChangePassword(String code, ChangePasswordRequestDTO changePasswordRequestDTO){
        Optional<RedefinirSenha> redefinirSenha = redefinirSenhaRepository.findByVerificationCode(code);
        if (redefinirSenha.isEmpty() || redefinirSenha.orElseThrow().getExpirationCodeTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Código de validação inválido!");
        }
        Usuario usuario = redefinirSenha.orElseThrow().getUsuario();
        String newPassword = new BCryptPasswordEncoder().encode(changePasswordRequestDTO.getNewPassword());
        usuario.setPassword(newPassword);
        usuarioRepository.save(usuario);
        redefinirSenhaRepository.deleteById(redefinirSenha.get().getId());
    }
}
