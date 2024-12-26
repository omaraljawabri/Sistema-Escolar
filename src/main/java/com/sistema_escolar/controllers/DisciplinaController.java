package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.CreateDisciplinaRequestDTO;
import com.sistema_escolar.services.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/disciplina")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints ligados a criação de Disciplinas", name = "Disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @Operation(summary = "Endpoint responsável por fazer a criação de uma disciplina, com autorização apenas do ADMIN",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping
    public ResponseEntity<Void> createDisciplina(@RequestBody CreateDisciplinaRequestDTO createDisciplinaRequestDTO){
        disciplinaService.createDisciplina(createDisciplinaRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
