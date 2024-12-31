package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.ProvaController;
import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.request.PublishProvaRequestDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.UserDoesntBelongException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.services.ProvaService;
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

import static com.sistema_escolar.utils.EntityUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class ProvaControllerTest {

    @InjectMocks
    private ProvaController provaController;

    @Mock
    private ProvaService provaService;

    @BeforeEach
    void setup(){
        when(provaService.criarProva(ArgumentMatchers.any(ProvaPostRequestDTO.class), ArgumentMatchers.any(Usuario.class)))
                .thenReturn(criarProvaResponseDTO());
        when(provaService.atualizarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ProvaPutRequestDTO.class), ArgumentMatchers.any(Usuario.class)))
                .thenReturn(criarProvaResponseDTO());
        doNothing().when(provaService).publicarProva(ArgumentMatchers.any(PublishProvaRequestDTO.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        when(provaService.getProvaAvaliada(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class)))
                .thenReturn(criarProvaAvaliadaResponseDTO());
    }

    private void mockAuthentication(){
        Usuario usuario = new Usuario();
        usuario.setEmail("fulano@gmail.com");
        usuario.setPassword("fulano");
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("criarProva deve cadastrar uma prova e retornar um ProvaResponseDTO quando bem sucedido")
    void criarProva_CadastraUmaProvaERetornaProvaResponseDTO_QuandoBemSucedido() {
        mockAuthentication();
        ResponseEntity<ProvaResponseDTO> provaResponseDTOResponseEntity =
                provaController.criarProva(criarProvaPostRequestDTO());
        assertThat(provaResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(provaResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaResponseDTOResponseEntity.getBody().getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTOResponseEntity.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("criarProva deve lançar uma UserNotFoundException quando o professor não estiver cadastrado")
    void criarProva_LancaUserNotFoundException_QuandoProfessorNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(provaService).criarProva(ArgumentMatchers.any(ProvaPostRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaController.criarProva(criarProvaPostRequestDTO()))
                .withMessage("Professor não foi encontrado");
    }

    @Test
    @DisplayName("criarProva deve lançar uma UserDoesntBelongException quando professor não estiver vinculado a nenhuma turma")
    void criarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstaVinculadoANenhumaTurma(){
        mockAuthentication();
        doThrow(new UserDoesntBelongException("Professor deve estar vinculado a uma turma para criar uma prova"))
                .when(provaService).criarProva(ArgumentMatchers.any(ProvaPostRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaController.criarProva(criarProvaPostRequestDTO()))
                .withMessage("Professor deve estar vinculado a uma turma para criar uma prova");
    }

    @Test
    @DisplayName("criarProva deve lançar uma EntityNotFoundException quando a questão tiver id e esse id não existir no banco de dados")
    void criarProva_LancaEntityNotFoundException_QuandoQuestaoTiverIdEIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Questão não existe"))
                .when(provaService).criarProva(ArgumentMatchers.any(ProvaPostRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaController.criarProva(criarProvaPostRequestDTO()))
                .withMessage("Questão não existe");
    }

    @Test
    @DisplayName("atualizarProva deve atualizar uma prova e retornar um ProvaResponseDTO quando bem sucedido")
    void atualizarProva_AtualizaProvaERetornaProvaResponseDTO_QuandoBemSucedido() {
        mockAuthentication();
        ResponseEntity<ProvaResponseDTO> provaResponseDTOResponseEntity = provaController.atualizarProva(1L, criarProvaPutRequestDTO());
        assertThat(provaResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(provaResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaResponseDTOResponseEntity.getBody().getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTOResponseEntity.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando prova não pertencer ao professor ou id da prova não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Prova não pertence a esse professor ou id da prova não existe"))
                .when(provaService).atualizarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ProvaPutRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaController.atualizarProva(1L, criarProvaPutRequestDTO()))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando o id da questão a ser atualizada não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Id da questão não existe"))
                .when(provaService).atualizarProva(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ProvaPutRequestDTO.class), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaController.atualizarProva(1L, criarProvaPutRequestDTO()))
                .withMessage("Id da questão não existe");
    }

    @Test
    @DisplayName("publicarProva deve publicar uma prova quando a operação for bem sucedida")
    void publicarProva_PublicaUmaProva_QuandoBemSucedido() {
        mockAuthentication();
        assertThatCode(() -> provaController.publicarProva(1L, criarPublishProvaRequestDTO()))
                .doesNotThrowAnyException();
        ResponseEntity<Void> responseEntity = provaController.publicarProva(1L, criarPublishProvaRequestDTO());
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserNotFoundException quando o professor com id passado não existir")
    void publicarProva_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(provaService).publicarProva(ArgumentMatchers.any(PublishProvaRequestDTO.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaController.publicarProva(2L, criarPublishProvaRequestDTO()))
                .withMessage("Professor não foi encontrado");
    }

    @Test
    @DisplayName("publicarProva deve lançar uma EntityNotFoundException quando a prova não pertencer ao professor ou id da prova não existir")
    void publicarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Prova não pertence a esse professor ou id da prova não existe"))
                .when(provaService).publicarProva(ArgumentMatchers.any(PublishProvaRequestDTO.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaController.publicarProva(2L, criarPublishProvaRequestDTO()))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserDoesntBelongException quando professor não pertencer a nenhuma turma")
    void publicarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverEmNenhumaTurma(){
        mockAuthentication();
        doThrow(new UserDoesntBelongException("Professor não está vinculado a uma turma"))
                .when(provaService).publicarProva(ArgumentMatchers.any(PublishProvaRequestDTO.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaController.publicarProva(1L, criarPublishProvaRequestDTO()))
                .withMessage("Professor não está vinculado a uma turma");
    }


    @Test
    @DisplayName("buscarProvaAvaliada deve retornar uma ProvaAvaliadaResponseDTO quando busca por prova avaliada é bem sucedida")
    void buscarProvaAvaliada_RetornaProvaAvaliadaResponseDTO_QuandoBuscaPorProvaAvaliadaEBemSucedida() {
        mockAuthentication();
        ResponseEntity<ProvaAvaliadaResponseDTO> provaAvaliadaResponseDTOResponseEntity = provaController.buscarProvaAvaliada(1L);
        assertThat(provaAvaliadaResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(provaAvaliadaResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(provaAvaliadaResponseDTOResponseEntity.getBody().getProvaId()).isEqualTo(1L);
        assertThat(provaAvaliadaResponseDTOResponseEntity.getBody().getQuestoesAvaliadas()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma UserNotFoundException quando estudante não existir")
    void buscarProvaAvaliada_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(provaService).getProvaAvaliada(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaController.buscarProvaAvaliada(2L))
                .withMessage("Estudante não encontrado");
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma EntityNotFoundException quando id da prova buscada não existir")
    void buscarProvaAvaliada_LancaEntityNotFoundException_QuandoProvaIdNaoExistir(){
        mockAuthentication();
        doThrow(new EntityNotFoundException("Id da prova não existe"))
                .when(provaService).getProvaAvaliada(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaController.buscarProvaAvaliada(2L))
                .withMessage("Id da prova não existe");
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma UserDoesntBelongException quando prova buscada não tiver sido feita pelo estudante")
    void buscarProvaAvaliada_LancaUserDoesntBelongException_QuandoProvaNaoTiverSidoFeitaPeloEstudante(){
        mockAuthentication();
        doThrow(new UserDoesntBelongException("Estudante não fez esta prova"))
                .when(provaService).getProvaAvaliada(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaController.buscarProvaAvaliada(2L))
                .withMessage("Estudante não fez esta prova");
    }
}