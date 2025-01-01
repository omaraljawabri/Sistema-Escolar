package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CodeRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.infra.handlers.ErrorMessage;
import com.sistema_escolar.services.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/v1/turma")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints responsáveis por realizar operações relacionadas com a Turma", name="Turmas")
public class TurmaController {

    private final TurmaService turmaService;

    @Operation(summary = "Endpoint responsável por cadastrar uma turma em uma disciplina de acordo com o id da disciplina passado",
            description = "Necessita da ROLE_ADMIN para ser acessado",
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
    @PostMapping
    public ResponseEntity<Void> criarTurma(@RequestBody CreateTurmaRequestDTO createTurmaRequestDTO){
        turmaService.criarTurma(createTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Endpoint responsável por adicionar um estudante a uma turma",
            description = "Necessita da ROLE_ADMIN para ser acessado",
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
    @PostMapping("/estudante")
    public ResponseEntity<Void> addEstudante(@RequestBody AddTurmaRequestDTO addTurmaRequestDTO){
        turmaService.addEstudante(addTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por adicionar um professor a uma turma",
            description = "Necessita da ROLE_ADMIN para ser acessado",
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
    @PostMapping("/professor")
    public ResponseEntity<Void> addProfessor(@RequestBody AddTurmaRequestDTO addTurmaRequestDTO){
        turmaService.addProfessor(addTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por gerar um código para que outros usuários entrem em uma turma",
            description = "Necessita da ROLE_ADMIN para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @PostMapping("/generate-code/admin")
    public ResponseEntity<CodeResponseDTO> gerarCodigo(@RequestBody TurmaRequestDTO turmaRequestDTO){
        return ResponseEntity.ok(turmaService.gerarCodigo(turmaRequestDTO));
    }

    @Operation(summary = "Endpoint responsável por gerar um código para que estudantes entrem na turma do professor",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping("/generate-code/professor")
    public ResponseEntity<CodeResponseDTO> gerarCodigo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(turmaService.gerarCodigo(usuario));
    }

    @Operation(summary = "Endpoint responsável por adicionar um estudante ou professor em uma turma de acordo com o código da turma passado",
            description = "Necessita da ROLE_PROFESSOR ou ROLE_ESTUDANTE para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping("/join")
    public ResponseEntity<Void> entrarTurma(@RequestBody CodeRequestDTO codeRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        turmaService.entrarTurma(codeRequestDTO, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
