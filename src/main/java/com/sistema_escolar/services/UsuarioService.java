package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Admin;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.enums.UserRole;
import com.sistema_escolar.repositories.AdminRepository;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;

    public void registerUser(RegisterRequestDTO registerRequestDTO){
        if (usuarioRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("Email enviado já existe"); //Do custom exception
        }
        String password = new BCryptPasswordEncoder().encode(registerRequestDTO.getPassword());
        if (registerRequestDTO.getRole() == UserRole.ADMIN){
            Admin admin = new Admin(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole());
            adminRepository.save(admin);
        } else if (registerRequestDTO.getRole() == UserRole.PROFESSOR){
            Professor professor = new Professor(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole());
            professorRepository.save(professor);
        } else{
            Estudante estudante = new Estudante(registerRequestDTO.getEmail(), password, registerRequestDTO.getRole());
            estudanteRepository.save(estudante);
        }
    }


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, String token){
        return LoginResponseDTO.builder().email(loginRequestDTO.getEmail()).token(token).build();
    }
}
