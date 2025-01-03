package com.sistema_escolar.unit.service;

import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.ProfessorRepository;
import com.sistema_escolar.services.ProfessorService;
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
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepository;

    @BeforeEach
    void setup(){
        when(professorRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarProfessor()));
    }

    @Test
    @DisplayName("buscarPorId deve retornar um Professor quando o id buscado existir")
    void buscarPorId_RetornaProfessor_QuandoIdBuscadoExistir() {
        Professor professor = professorService.buscarPorId(1L);
        assertThat(professor).isNotNull();
        assertThat(professor.getId()).isEqualTo(1L);
        assertThat(professor.getNome()).isEqualTo("Professor");
    }

    @Test
    @DisplayName("buscarPorId deve lançar uma UserNotFoundException quando o id buscado não existir")
    void buscarPorId_LancaUserNotFoundException_QuandoIdBuscadoNaoExistir(){
        when(professorRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> professorService.buscarPorId(4L))
                .withMessage("Professor não foi encontrado");
    }
}