package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.MudarSenhaEmailRequestDTO;
import com.sistema_escolar.dtos.request.MudarSenhaRequestDTO;
import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegistrarRequestDTO;
import com.sistema_escolar.dtos.response.LoginResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.infra.handlers.ErrorMessage;
import com.sistema_escolar.infra.security.TokenService;
import com.sistema_escolar.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(description = "Endpoints relacionados a autenticação, login e mudança de senha do usuário", name = "Autenticação")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Endpoint responsável por fazer o registro do usuário e enviar um e-mail de validação da conta",
    method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegistrarRequestDTO registrarRequestDTO){
        authenticationService.registrarUsuario(registrarRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por fazer o login do usuário e retornar um token JWT para acesso aos demais endpoints",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)", content = @Content())
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getSenha());
        Authentication auth = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String token = tokenService.gerarToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO, token));
    }

    @Operation(summary = "Endpoint responsável por fazer a validação do código recebido pelo usuário via e-mail ao fazer o registro",
            method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Erro no código passado(Forbidden)"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @GetMapping("/verificar")
    public ResponseEntity<Void> verificarCodigo(
            @RequestParam("code") String code
    ){
        authenticationService.verificarCodigo(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por fazer a requisição da mudança de senha pelo usuário e enviar um e-mail de validação ao mesmo",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(value = "/mudar-senha/requisicao", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> pedirMudancaDeSenha(@RequestBody MudarSenhaEmailRequestDTO mudarSenhaEmailRequestDTO){
        authenticationService.mudarSenha(mudarSenhaEmailRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Endpoint responsável por validar o link enviado ao e-mail do usuário e receber a nova senha para mudança",
            method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar operação(Internal server error)")
    })
    @PostMapping(value = "/mudar-senha/verificar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> verificarMudancaDeSenha(
            @RequestParam("code") String code,
            @RequestBody MudarSenhaRequestDTO mudarSenhaRequestDTO
    ){
        authenticationService.verificarMudarSenha(code, mudarSenhaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
