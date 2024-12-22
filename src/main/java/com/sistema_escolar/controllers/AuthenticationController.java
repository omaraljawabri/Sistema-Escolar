package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.ChangePasswordEmailRequestDTO;
import com.sistema_escolar.dtos.request.ChangePasswordRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.infra.security.TokenService;
import com.sistema_escolar.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        authenticationService.registerUser(registerRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        Authentication auth = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO, token));
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyCode(@RequestParam("code") String code){
        authenticationService.verifyCode(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/change-password/request")
    public ResponseEntity<Void> requestChangePassword(@RequestBody ChangePasswordEmailRequestDTO changePasswordEmailRequestDTO){
        authenticationService.changePassword(changePasswordEmailRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/change-password/verify")
    public ResponseEntity<Void> verifyChangePassword(@RequestParam("code") String code, @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        authenticationService.verifyChangePassword(code, changePasswordRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
