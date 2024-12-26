package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublishProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prova")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProvaResponseDTO> createProva(@RequestBody ProvaPostRequestDTO provaPostRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.createProva(provaPostRequestDTO, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProvaResponseDTO> updateProva(@PathVariable Long id, @RequestBody ProvaPutRequestDTO provaPutRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.updateProva(id, provaPutRequestDTO, usuario));
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<Void> publishProva(@PathVariable Long id, @RequestBody PublishProvaRequestDTO publishProvaRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        provaService.publishProva(publishProvaRequestDTO, id, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
