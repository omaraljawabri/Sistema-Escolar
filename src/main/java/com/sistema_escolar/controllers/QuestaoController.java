package com.sistema_escolar.controllers;

import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.services.QuestaoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questao")
@RequiredArgsConstructor
@SecurityRequirement(name = "securityConfig")
public class QuestaoController {

    private final QuestaoService questaoService;

    @GetMapping
    public ResponseEntity<Page<QuestaoResponseDTO>> findQuestoes(@RequestParam int pagina,
                                                                 @RequestParam int quantidade){
        return ResponseEntity.ok(questaoService.findQuestoes(pagina, quantidade));
    }
}
