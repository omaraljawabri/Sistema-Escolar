package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.ProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prova")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProvaResponseDTO> createProva(@RequestBody ProvaRequestDTO provaRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.createProva(provaRequestDTO, usuario));
    }

}
