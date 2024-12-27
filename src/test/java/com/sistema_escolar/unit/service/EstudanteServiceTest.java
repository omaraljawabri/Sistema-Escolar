package com.sistema_escolar.unit.service;

import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.services.EstudanteService;
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
class EstudanteServiceTest {

    @InjectMocks
    private EstudanteService estudanteService;

    @Mock
    private EstudanteRepository estudanteRepository;

    @BeforeEach
    void setup(){
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarEstudante()));
    }

    @Test
    @DisplayName("buscarPorId deve retornar um Estudante quando o id passado existir")
    void buscarPorId_RetornaEstudante_QuandoIdPassadoExistir() {
        Estudante estudante = estudanteService.buscarPorId(1L);
        assertThat(estudante).isNotNull();
        assertThat(estudante.getEmail()).isEqualTo("ciclano@gmail.com");
        assertThat(estudante.getFirstName()).isEqualTo("Ciclano");
    }

    @Test
    @DisplayName("buscarPorId deve lançar uma UserNotFoundException quando o id passado não existir no banco de dados")
    void buscarPorId_LancaUserNotFoundException_QuandoIdNaoExistir(){
        when(estudanteRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> estudanteService.buscarPorId(2L));
    }
}