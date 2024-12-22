package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Admin;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.enums.UserRole;
import com.sistema_escolar.repositories.AdminRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
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
    private final MailService mailService;

    @Transactional
    public void registerUser(RegisterRequestDTO registerRequestDTO){
        if (usuarioRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("Email enviado já existe"); //Do custom exception
        }
        String verificationCode = UUID.randomUUID().toString();
        LocalDateTime expirationCodeTime = LocalDateTime.now().plusHours(24);
        String password = new BCryptPasswordEncoder().encode(registerRequestDTO.getPassword());
        if (registerRequestDTO.getRole() == UserRole.ADMIN){
            Admin admin
                    = new Admin(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode, expirationCodeTime, false);
            adminRepository.save(admin);
        } else if (registerRequestDTO.getRole() == UserRole.PROFESSOR){
            Professor professor
                    = new Professor(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode, expirationCodeTime, false);
            professorRepository.save(professor);
        } else{
            Estudante estudante
                    = new Estudante(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole(), verificationCode, expirationCodeTime, false);
            estudanteRepository.save(estudante);
        }
        mailService.sendVerificationEmail(registerRequestDTO, verificationCode);
    }

    public void verifyCode(String code){
        Optional<Usuario> usuario = usuarioRepository.findByVerificationCode(code);
        if (usuario.isEmpty() || usuario.orElseThrow().getExpirationCodeTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Código de verificação inválido"); //Make customized exception
        }
        usuario.orElseThrow().setIsVerified(true);
        usuario.orElseThrow().setVerificationCode(null);
        usuario.orElseThrow().setExpirationCodeTime(null);
        usuarioRepository.save(usuario.get());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, String token){
        return LoginResponseDTO.builder().email(loginRequestDTO.getEmail()).token(token).build();
    }
}
