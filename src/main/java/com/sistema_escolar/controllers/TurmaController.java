package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.AddTurmaRequestDTO;
import com.sistema_escolar.dtos.request.CreateTurmaRequestDTO;
import com.sistema_escolar.services.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
