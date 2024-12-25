package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.RespostaProvaRequestDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.RespostaProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resposta-prova")
@RequiredArgsConstructor
public class RespostaProvaController {

    private final RespostaProvaService respostaProvaService;

    @PostMapping("/{id}")
    public ResponseEntity<Void> responderProva(@PathVariable Long id, @RequestBody RespostaProvaRequestDTO respostaProvaRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        respostaProvaService.responderProva(id, respostaProvaRequestDTO, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
