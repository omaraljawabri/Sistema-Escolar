package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.NotaController;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.QuestionErrorException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.services.NotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class NotaControllerTest {

    @InjectMocks
    private NotaController notaController;

    @Mock
    private NotaService notaService;

    @BeforeEach
    void setup(){
        when(notaService.avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong()))
                .thenReturn(new NotaResponseDTO(10D));
    }

    @Test
    @DisplayName("avaliarProva deve retornar um NotaResponseDTO quando uma prova for avaliada com sucesso")
    void avaliarProva_RetornaNotaResponseDTO_QuandoAProvaEAvaliadaComSuceso() {
        mockAuthentication();
        ResponseEntity<NotaResponseDTO> notaResponseDTO = notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO()));
        assertThat(notaResponseDTO.getBody()).isNotNull();
        assertThat(notaResponseDTO.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notaResponseDTO.getBody().getNotaProva()).isEqualTo(10D);
    }

    @Test
    @DisplayName("avaliarProva lança uma UserNotFoundException quando o estudante não for encontrado")
    void avaliarProva_LancaUserNotFoundException_QuandoEstudanteNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(notaService).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 2L, List.of(criarNotaRequestDTO())))
                .withMessage("Estudante não encontrado");
    }

    @Test
    @DisplayName("avaliarProva lança uma UserNotFoundException quando professor não for encontrado")
    void avaliarProva_LancaUserNotFoundException_QuandoProfessorNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(notaService).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO())))
                .withMessage("Professor não foi encontrado");
    }

    @Test
    @DisplayName("avaliarProva lança uma EntityNotFoundException quando prova não pertencer ao professor ou id da prova não existir")
    void avaliarProva_LancaEntityNotFoundException_QuandoProvaNaoPertencerAoProfessorOuProvaIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Prova não pertence a esse professor ou id da prova não existe"))
                .when(notaService).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO())))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
    }

    @Test
    @DisplayName("avaliarProva lança uma QuestionErrorException quando o valor atribuido a questão for maior do que seu valor máximo")
    void avaliarProva_LancaQuestionErrorException_QuandoNotaDaQuestaoForMaiorQueSeuValorMaximo(){
        mockAuthentication();
        doThrow(new QuestionErrorException("Nota da questão é maior do que seu valor máximo"))
                .when(notaService ).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(QuestionErrorException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO())))
                .withMessage("Nota da questão é maior do que seu valor máximo");
    }

    @Test
    @DisplayName("avaliarProva lança uma EntityNotFoundException quando id da questão passada não existir")
    void avaliarProva_LancaEntityNotFoundException_QuandoIdDaQuestaoNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Id da questão não existe"))
                .when(notaService).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO())))
                .withMessage("Id da questão não existe");
    }

    @Test
    @DisplayName("avaliarProva lança uma QuestionErrorException quando a questão não tiver sido respondida pelo estudante")
    void avaliarProva_LancaQuestionErrorException_QuandoQuestaoNaoTiverSidoRespondidaPeloEstudante(){
        mockAuthentication();
        doThrow(new QuestionErrorException("Questão não foi respondida pelo estudante na prova com id passado"))
                .when(notaService).avaliarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(QuestionErrorException.class)
                .isThrownBy(() -> notaController.avaliarProva(1L, 1L, List.of(criarNotaRequestDTO())))
                .withMessage("Questão não foi respondida pelo estudante na prova com id passado");
    }
}