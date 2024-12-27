package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.NotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(description = "Endpoints responsáveis por avaliar uma prova e atribuir nota a ela", name = "Notas")
public class NotaController {

    private final NotaService notaService;

    @Operation(summary = "Endpoint responsável por avaliar a prova e atribuir nota a ela",
            description = "Necessita de ROLE_PROFESSOR para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping("/prova/{id}/{estudanteId}")
    public ResponseEntity<NotaResponseDTO> avaliarProva(@PathVariable Long id, @PathVariable Long estudanteId, @RequestBody List<NotaRequestDTO> notaRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(notaService.avaliarProva(id, notaRequestDTO, usuario, estudanteId));
    }
}
