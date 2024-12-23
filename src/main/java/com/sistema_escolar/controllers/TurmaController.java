package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CodeRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.dtos.request.TurmaRequestDTO;
import com.sistema_escolar.dtos.response.CodeResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/turma")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @PostMapping
    public ResponseEntity<Void> createTurma(@RequestBody CreateTurmaRequestDTO createTurmaRequestDTO){
        turmaService.createTurma(createTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/estudante")
    public ResponseEntity<Void> addEstudante(@RequestBody AddTurmaRequestDTO addTurmaRequestDTO){
        turmaService.addEstudante(addTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/professor")
    public ResponseEntity<Void> addProfessor(@RequestBody AddTurmaRequestDTO addTurmaRequestDTO){
        turmaService.addProfessor(addTurmaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/generate-code/admin")
    public ResponseEntity<CodeResponseDTO> generateCode(@RequestBody TurmaRequestDTO turmaRequestDTO){
        return ResponseEntity.ok(turmaService.generateCode(turmaRequestDTO));
    }

    @GetMapping("/generate-code/professor")
    public ResponseEntity<CodeResponseDTO> generateCode(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(turmaService.generateCode(usuario));
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinTurma(@RequestBody CodeRequestDTO codeRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        turmaService.joinTurma(codeRequestDTO, usuario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
