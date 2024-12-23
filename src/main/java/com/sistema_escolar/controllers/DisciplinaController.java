package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.request.CreateDisciplinaRequestDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.DisciplinaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/disciplina")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @PostMapping
    public ResponseEntity<Void> createDisciplina(@RequestBody CreateDisciplinaRequestDTO createDisciplinaRequestDTO){
        disciplinaService.createDisciplina(createDisciplinaRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
