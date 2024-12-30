package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.DisciplinaController;
import com.sistema_escolar.dtos.request.CreateDisciplinaRequestDTO;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.services.DisciplinaService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class DisciplinaControllerTest {

    @InjectMocks
    private DisciplinaController disciplinaController;

    @Mock
    private DisciplinaService disciplinaService;

    @BeforeEach
    void setup(){
        doNothing().when(disciplinaService).criarDisciplina(ArgumentMatchers.any(CreateDisciplinaRequestDTO.class));
    }

    @Test
    @DisplayName("criarDisciplina deve cadastrar uma disciplina no sistema quando for bem sucedido")
    void criarDisciplina_CadastraUmaDisciplinaNoSistema_QuandoBemSucedido() {
        assertThatCode(() -> disciplinaController.criarDisciplina(new CreateDisciplinaRequestDTO("Geografia")))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = disciplinaController.criarDisciplina(new CreateDisciplinaRequestDTO("Geografia"));
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("criarDisciplina deve lançar uma EntityAlreadyExistsException quando o nome da disciplina a ser cadastrada já existir")
    void criarDisciplina_LancaEntityAlreadyExistsException_QuandoNomeDaDisciplinaJaExistir(){
        doThrow(new EntityAlreadyExistsException("Nome da disciplina já existe"))
                .when(disciplinaService).criarDisciplina(ArgumentMatchers.any(CreateDisciplinaRequestDTO.class));
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> disciplinaController.criarDisciplina(new CreateDisciplinaRequestDTO("Matemática")))
                .withMessage("Nome da disciplina já existe");
    }
}