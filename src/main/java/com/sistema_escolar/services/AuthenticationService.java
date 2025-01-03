package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.MudarSenhaEmailRequestDTO;
import com.sistema_escolar.dtos.request.MudarSenhaRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegistrarRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.*;
import com.sistema_escolar.exceptions.AccountWasntValidatedException;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.exceptions.InvalidCodeException;
import com.sistema_escolar.exceptions.UserNotFoundException;
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
    public void registrarUsuario(RegistrarRequestDTO registrarRequestDTO){
        if (usuarioRepository.findByEmail(registrarRequestDTO.getEmail()).isPresent()){
            throw new EntityAlreadyExistsException("Email "+usuarioRepository.findByEmail(registrarRequestDTO.getEmail()).get().getEmail()+" já existe");
        }
        String verificationCode = UUID.randomUUID().toString();
        LocalDateTime codeExpirationTime = LocalDateTime.now().plusHours(24);
        String password = new BCryptPasswordEncoder().encode(registrarRequestDTO.getSenha());
        salvarUsuario(registrarRequestDTO, password, verificationCode, codeExpirationTime);
        String verificationLink = String.format("http://localhost:8080/api/v1/auth/verificar?code=%s", verificationCode);
        String subject = "Validação de cadastro";
        String textMessage = String.format("Olá, recebemos uma solicitação de cadastro na nossa plataforma utilizando este e-mail. %nCaso deseje validar sua conta em nossa plataforma, clique no link abaixo: %n%s", verificationLink);
        mailService.enviarEmail(registrarRequestDTO.getEmail(), subject, textMessage);
    }

    public void verificarCodigo(String code){
        Optional<Usuario> usuario = usuarioRepository.findByCodigoDeVerificacao(code);
        if (usuario.isEmpty() || usuario.orElseThrow().getTempoDeExpiracaoCodigo().isBefore(LocalDateTime.now())){
            throw new InvalidCodeException("Código de verificação inválido");
        }
        usuario.orElseThrow().setVerificado(true);
        usuario.orElseThrow().setCodigoDeVerificacao(null);
        usuario.orElseThrow().setTempoDeExpiracaoCodigo(null);
        usuarioRepository.save(usuario.get());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, String token){
        Usuario usuario
                = usuarioRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email enviado não existe!"));
        if (Boolean.FALSE.equals(usuario.getVerificado())){
            throw new AccountWasntValidatedException("Conta não foi validada!");
        }
        return LoginResponseDTO.builder().email(loginRequestDTO.getEmail()).token(token).build();
    }

    @Transactional
    public void mudarSenha(MudarSenhaEmailRequestDTO mudarSenhaEmailRequestDTO){
        Usuario usuario
                = usuarioRepository.findByEmail(mudarSenhaEmailRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email enviado não está cadastrado"));
        String verificationCode = UUID.randomUUID().toString();
        RedefinirSenha redefinirSenha = RedefinirSenha.builder().codigoDeVerificacao(verificationCode).tempoDeExpiracaoCodigo(LocalDateTime.now().plusHours(24))
                .usuario(usuario).build();
        redefinirSenhaRepository.save(redefinirSenha);
        String verificationLink = String.format("http://localhost:8080/api/v1/auth/change-password/verificar?code=%s",verificationCode);
        String subject = "Redefinição de senha";
        String textMessage = String.format("Olá, recebemos seu pedido para redefinição de senha!%nClique no link abaixo para prosseguir com o processo!%n%s",verificationLink);
        mailService.enviarEmail(mudarSenhaEmailRequestDTO.getEmail(), subject, textMessage);
    }

    @Transactional
    public void verificarMudarSenha(String code, MudarSenhaRequestDTO mudarSenhaRequestDTO){
        Optional<RedefinirSenha> redefinirSenha = redefinirSenhaRepository.findByCodigoDeVerificacao(code);
        if (redefinirSenha.isEmpty() || redefinirSenha.orElseThrow().getTempoDeExpiracaoCodigo().isBefore(LocalDateTime.now())){
            throw new InvalidCodeException("Código de validação inválido!");
        }
        Usuario usuario = redefinirSenha.orElseThrow().getUsuario();
        String newPassword = new BCryptPasswordEncoder().encode(mudarSenhaRequestDTO.getNovaSenha());
        usuario.setSenha(newPassword);
        usuarioRepository.save(usuario);
        redefinirSenhaRepository.deleteById(redefinirSenha.get().getId());
    }

    private void salvarUsuario(RegistrarRequestDTO registrarRequestDTO, String password, String verificationCode,
                               LocalDateTime codeExpirationTime){
        if (registrarRequestDTO.getRole() == UserRole.ADMIN){
            Admin admin
                    = new Admin(registrarRequestDTO.getEmail(), password, registrarRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registrarRequestDTO.getNome(), registrarRequestDTO.getSobrenome());
            adminRepository.save(admin);
        } else if (registrarRequestDTO.getRole() == UserRole.PROFESSOR){
            Professor professor
                    = new Professor(registrarRequestDTO.getEmail(), password, registrarRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registrarRequestDTO.getNome(), registrarRequestDTO.getSobrenome());
            professorRepository.save(professor);
        } else{
            Estudante estudante
                    = new Estudante(registrarRequestDTO.getEmail(), password, registrarRequestDTO.getRole(), verificationCode,
                    codeExpirationTime, false, registrarRequestDTO.getNome(), registrarRequestDTO.getSobrenome());
            estudanteRepository.save(estudante);
        }
    }
}
