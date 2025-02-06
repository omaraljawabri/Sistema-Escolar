package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.CriarDisciplinaRequestDTO;
import com.sistema_escolar.infra.handlers.ErrorMessage;
import com.sistema_escolar.services.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/disciplina")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
@CrossOrigin("http://localhost:4200")
@Tag(description = "Endpoints ligados a criação de Disciplinas", name = "Disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @Operation(summary = "Endpoint responsável por fazer a criação de uma disciplina",
            description = "Necessita da ROLE_ADMIN para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> criarDisciplina(@RequestBody CriarDisciplinaRequestDTO criarDisciplinaRequestDTO){
        disciplinaService.criarDisciplina(criarDisciplinaRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
