package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.RespostaProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaRespondidaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.infra.handlers.ErrorMessage;
import com.sistema_escolar.services.RespostaProvaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resposta-prova")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints responsáveis por realizar operações relacionadas com RespostaProva", name = "Respostas Prova")
public class RespostaProvaController {

    private final RespostaProvaService respostaProvaService;

    @Operation(summary = "Endpoint responsável por cadastrar a resposta do estudante a uma prova",
            description = "Necessita da ROLE_ESTUDANTE para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping("/{id}")
    public ResponseEntity<Void> responderProva(
            @PathVariable Long id,
            @RequestBody RespostaProvaRequestDTO respostaProvaRequestDTO
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        respostaProvaService.responderProva(id, respostaProvaRequestDTO, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por buscar as provas respondidas pelos estudantes de acordo com o id da prova",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping("/{provaId}")
    public ResponseEntity<List<ProvaRespondidaResponseDTO>> provasRespondidas(
            @PathVariable Long provaId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(respostaProvaService.provasRespondidas(usuario, provaId));
    }
}
