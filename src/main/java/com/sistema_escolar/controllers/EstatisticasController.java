package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.response.EstatisticasEstudanteResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasGeraisResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.infra.handlers.ErrorMessage;
import com.sistema_escolar.services.EstatisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estatisticas")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints responsáveis por prover estatísticas ligadas a notas, médias, quantidade de turmas, etc",
        name = "Estatísticas")
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    @Operation(summary = "Endpoint responsável por buscar estatísticas relacionadas ao desempenho de um estudante",
            description = "Necessita da ROLE_ESTUDANTE para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping("/estudante")
    public ResponseEntity<EstatisticasEstudanteResponseDTO> estatisticasDoEstudante(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(estatisticasService.estatisticasDoEstudante(usuario));
    }

    @Operation(summary = "Endpoint responsável por buscar estatísticas gerais do sistema, como número de disciplinas e turmas",
            description = "Necessita da ROLE_ADMIN para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping("/geral")
    public ResponseEntity<EstatisticasGeraisResponseDTO> estatisticasGerais(){
        return ResponseEntity.ok(estatisticasService.estatisticasGerais());
    }

    @Operation(summary = "Endpoint responsável por buscar estatísticas relacionadas ao desempenho de uma turma pelo seu id",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping(value = "/turma/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasDaTurma(
            @PathVariable Long id
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(estatisticasService.estatisticasDaTurma(id, usuario));
    }
}
