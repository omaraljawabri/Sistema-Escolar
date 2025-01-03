package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.CriarDisciplinaRequestDTO;
import com.sistema_escolar.entities.Disciplina;
import com.sistema_escolar.exceptions.EntityAlreadyExistsException;
import com.sistema_escolar.repositories.DisciplinaRepository;
import com.sistema_escolar.services.DisciplinaService;
import com.sistema_escolar.utils.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class DisciplinaServiceTest {

    @InjectMocks
    private DisciplinaService disciplinaService;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @BeforeEach
    void setup(){
        when(disciplinaRepository.findByNome(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(EntityUtils.criarDisciplina()));
        when(disciplinaRepository.save(ArgumentMatchers.any(Disciplina.class)))
                .thenReturn(EntityUtils.criarDisciplina());
    }

    @Test
    @DisplayName("criarDisciplina deve cadadastrar uma disciplina no banco de dados quando bem sucedido")
    void criarDisciplina_CadastrarUmaDisciplina_QuandoBemSucedido() {
        when(disciplinaRepository.findByNome(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatCode(() -> disciplinaService.criarDisciplina(new CriarDisciplinaRequestDTO("Geografia")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("criarDisciplina deve lançar uma EntityAlreadyExistsException quando o nome da disciplina já existir no banco de dados")
    void criarDisciplina_LancaEntityAlreadyExistsException_QuandoDisciplinaJaExistir(){
        assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> disciplinaService.criarDisciplina(new CriarDisciplinaRequestDTO("Matemática")));
    }
}