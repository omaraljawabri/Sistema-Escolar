package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.services.QuestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questao")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
@Tag(description = "Endpoints responsáveis por realizar operações relacionadas a Questao", name = "Questões")
public class QuestaoController {

    private final QuestaoService questaoService;

    @Operation(summary = "Endpoint responsável por buscar as questões já cadastrar de forma paginada",
            description = "Necessita da ROLE_PROFESSOR para ser acessado",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Usuário não foi autorizado", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão necessária para realizar operação", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<QuestaoResponseDTO>> buscarQuestoes(
            @Parameter(
                    name = "pagina",
                    description = "Número da página que será acessada",
                    required = true,
                    schema = @Schema(example = "1", type = "int")
            )
            @RequestParam int pagina,
            @Parameter(
                    name = "quantidade",
                    description = "Quantidade de questões que serão acessadas",
                    required = true,
                    schema = @Schema(example = "10", type = "int")
            )
            @RequestParam int quantidade
    ){
        return ResponseEntity.ok(questaoService.buscarQuestoes(pagina, quantidade));
    }
}
