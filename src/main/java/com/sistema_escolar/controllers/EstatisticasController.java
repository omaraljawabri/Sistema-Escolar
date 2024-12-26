package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.response.EstatisticasEstudanteResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasGeraisResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.EstatisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estatisticas")
@RequiredArgsConstructor
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    @GetMapping("/turma/{id}")
    public ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasDaTurma(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(estatisticasService.estatisticasDaTurma(id, usuario));
    }

    @GetMapping("/estudante")
    public ResponseEntity<EstatisticasEstudanteResponseDTO> estatisticasDoEstudante(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(estatisticasService.estatisticasDoEstudante(usuario));
    }

    @GetMapping("/geral")
    public ResponseEntity<EstatisticasGeraisResponseDTO> estatisticasGerais(){
        return ResponseEntity.ok(estatisticasService.estatisticasGerais());
    }
}
