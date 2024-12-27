package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublishProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.ProvaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints responsáveis por operações ligadas a prova", name = "Provas")
public class ProvaController {

    private final ProvaService provaService;

    @Operation(summary = "Endpoint responsável por criar uma prova e suas questões",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProvaResponseDTO> createProva(@RequestBody ProvaPostRequestDTO provaPostRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.createProva(provaPostRequestDTO, usuario));
    }

    @Operation(summary = "Endpoint responsável por atualizar uma prova e suas questões",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProvaResponseDTO> updateProva(
            @Parameter(
                    name = "id",
                    description = "Identificador único da prova",
                    required = true,
                    schema = @Schema(type = "Long", example = "1")
            )
            @PathVariable Long id,
            @RequestBody ProvaPutRequestDTO provaPutRequestDTO
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.updateProva(id, provaPutRequestDTO, usuario));
    }

    @Operation(summary = "Endpoint responsável por publicar uma prova pelo seu id e seu tempo até que expire",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(value = "/publish/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> publishProva(
            @Parameter(
                    description = "Identificador único da prova",
                    name = "id",
                    required = true,
                    schema = @Schema(example = "1", type = "Long")
            )
            @PathVariable Long id,
            @RequestBody PublishProvaRequestDTO publishProvaRequestDTO
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        provaService.publishProva(publishProvaRequestDTO, id, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por buscar uma prova do estudante já avaliada pelo seu id",
            description = "Necessita da ROLE_ESTUDANTE para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @GetMapping(value = "/avaliada/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProvaAvaliadaResponseDTO> getProvaAvaliada(
            @Parameter(
                    name = "id",
                    description = "Identificador único da prova",
                    required = true,
                    schema = @Schema(example = "1", type = "Long")
            )
            @PathVariable Long id
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(provaService.getProvaAvaliada(id, usuario));
    }
}
