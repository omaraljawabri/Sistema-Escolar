package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.RespostaProvaController;
import com.sistema_escolar.dtos.request.RespostaProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaRespondidaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.*;
import com.sistema_escolar.services.RespostaProvaService;
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

import java.util.Collections;
import java.util.List;

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class RespostaProvaControllerTest {

    @InjectMocks
    private RespostaProvaController respostaProvaController;

    @Mock
    private RespostaProvaService respostaProvaService;

    @BeforeEach
    void setup(){
        doNothing().when(respostaProvaService)
                .responderProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        when(respostaProvaService.provasRespondidas(ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong()))
                .thenReturn(List.of(criarProvaRespondidaResponseDTO()));
    }

    @Test
    @DisplayName("responderProva deve cadastrar a resposta de uma prova quando bem sucedido")
    void responderProva_CadastraARespostaDeUmaProva_QuandoBemSucedido() {
        mockAuthentication();
        assertThatCode(() -> respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO()))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO());
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("responderProva deve lançar uma UserNotFoundException quando o id do estudante que respondeu a prova não existir")
    void responderProva_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(respostaProvaService).responderProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO()))
                .withMessage("Estudante não encontrado");
    }

    @Test
    @DisplayName("responderProva deve lançar uma EntityNotFoundException quando o id da prova passado não existir")
    void responderProva_LancaEntityNotFoundException_QuandoProvaIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Id da prova não existe"))
                .when(respostaProvaService).responderProva(ArgumentMatchers.any(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> respostaProvaController.responderProva(2L, criarRespostaProvaRequestDTO()))
                .withMessage("Id da prova não existe");
    }

    @Test
    @DisplayName("responderProva deve lançar uma TestErrorException quando a prova respondida não tiver sido publicada ainda ou seu tempo de execução já tiver encerrado")
    void responderProva_LancaTestErrorException_QuandoProvaNaoTiverSidoPublicadaOuTiverExpirada(){
        mockAuthentication();
        doThrow(new TestErrorException("O tempo de prova já foi encerrado ou a prova não foi publicada ainda"))
                .when(respostaProvaService).responderProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(TestErrorException.class)
                .isThrownBy(() -> respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO()))
                .withMessage("O tempo de prova já foi encerrado ou a prova não foi publicada ainda");
    }

    @Test
    @DisplayName("responderProva deve lançar uma UserAlreadyBelongsToAnEntityException quando o estudante já tiver respondido a prova")
    void responderProva_LancaUserAlreadyBelongsToAnEntityException_QuandoEstudanteJaTiverRespondidoAProva(){
        mockAuthentication();
        doThrow(new UserAlreadyBelongsToAnEntityException("Estudante já respondeu a esta prova"))
                .when(respostaProvaService).responderProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO()))
                .withMessage("Estudante já respondeu a esta prova");
    }

    @Test
    @DisplayName("responderProva deve lançar uma EntityNotFoundException quando id da questão não existir ou questão não pertencer a prova")
    void responderProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistirOuQuestaoNaoPertencerAProva(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Id da questão não existe ou questão não pertence a esta prova"))
                .when(respostaProvaService).responderProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(RespostaProvaRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> respostaProvaController.responderProva(1L, criarRespostaProvaRequestDTO()))
                .withMessage("Id da questão não existe ou questão não pertence a esta prova");
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista de ProvaRespondidaResponseDTO quando a busca por provas respondidas for bem sucedida")
    void provasRespondidas_RetornaListaDeProvaRespondidaResponseDTO_QuandoBemSucedido() {
        mockAuthentication();
        ResponseEntity<List<ProvaRespondidaResponseDTO>> listResponseEntity = respostaProvaController.provasRespondidas(1L);
        assertThat(listResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponseEntity.getBody()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(listResponseEntity.getBody().getFirst().getNomeEstudante()).isEqualTo("Ciclano Sousa");
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista vazia quando a busca por provas respondidas não obtiver resultados")
    void provasRespondidas_RetornaListaVazia_QuandoNaoHouveremProvasRespondidas(){
        mockAuthentication();
        when(respostaProvaService.provasRespondidas(ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<ProvaRespondidaResponseDTO>> listResponseEntity = respostaProvaController.provasRespondidas(2L);
        assertThat(listResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponseEntity.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("provasRespondidas deve lançar uma UserNotFoundException quando o id do professor que solicitou a busca não existir")
    void provasRespondidas_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(respostaProvaService).provasRespondidas(ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> respostaProvaController.provasRespondidas(1L))
                .withMessage("Professor não foi encontrado");
    }

    @Test
    @DisplayName("provasRespondidas deve lançar uma EntityDoesntBelongToUserException quando a prova buscada não pertencer ao professor")
    void provasRespondidas_LancaEntityDoesntBelongToUserException_QuandoProvaNaoPertenceAoProfessor(){
        mockAuthentication();
        doThrow(new EntityDoesntBelongToUserException("Prova não pertence a esse usuário"))
                .when(respostaProvaService).provasRespondidas(ArgumentMatchers.any(Usuario.class), ArgumentMatchers.anyLong());
        assertThatExceptionOfType(EntityDoesntBelongToUserException.class)
                .isThrownBy(() -> respostaProvaController.provasRespondidas(1L))
                .withMessage("Prova não pertence a esse usuário");
    }
}