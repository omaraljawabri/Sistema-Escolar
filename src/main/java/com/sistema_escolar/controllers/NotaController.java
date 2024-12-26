package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.NotaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nota")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
public class NotaController {

    private final NotaService notaService;

    @PostMapping("/prova/{id}/{estudanteId}")
    public ResponseEntity<NotaResponseDTO> avaliarProva(@PathVariable Long id, @PathVariable Long estudanteId, @RequestBody List<NotaRequestDTO> notaRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(notaService.avaliarProva(id, notaRequestDTO, usuario, estudanteId));
    }
}
