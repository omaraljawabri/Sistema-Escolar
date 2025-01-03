package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.response.ProvaRespondidaResponseDTO;
import com.sistema_escolar.entities.Estudante;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.RespostaProva;
import com.sistema_escolar.exceptions.*;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.RespostaProvaRepository;
import com.sistema_escolar.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class RespostaProvaServiceTest {

    @InjectMocks
    private RespostaProvaService respostaProvaService;

    @Mock
    private RespostaProvaRepository respostaProvaRepository;

    @Mock
    private EstudanteService estudanteService;

    @Mock
    private ProfessorService professorService;

    @Mock
    private ProvaService provaService;

    @Mock
    private ProvaRepository provaRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setup(){
        Prova prova = criarProva();
        prova.setPublicado(true);
        Estudante estudante = criarEstudante();
        estudante.setId(2L);
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setEstudante(estudante);
        when(estudanteService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarEstudante());
        when(provaService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(prova);
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarQuestao()));
        when(questaoRepository.existsByIdAndProvasId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(true);
        when(respostaProvaRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(List.of(criarRespostaProva()));
        when(professorService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarProfessor());
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarProva()));
        when(respostaProvaRepository.findAllByProvaIdAndRespondidaTrue(ArgumentMatchers.anyLong()))
                .thenReturn(List.of(criarRespostaProva(), criarRespostaProva() ,respostaProva));
        when(respostaProvaRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(List.of(criarRespostaProva()));
    }

    @Test
    @DisplayName("responderProva deve cadastrar uma resposta de prova do usuário quando for bem sucedida")
    void responderProva_CadastraUmaRespostaDeProvaDoUsuario_QuandoBemSucedida() {
        RespostaProva respostaProva = criarRespostaProva();
        respostaProva.setRespondida(false);
        when(respostaProvaRepository.findByEstudanteIdAndProvaId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(List.of(respostaProva));
        assertThatCode(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .doesNotThrowAnyException();
        verify(mailService, times(1)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma UserNotFoundException quando o id do estudante não for encontrado")
    void responderProva_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(estudanteService).buscarPorId(ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("Estudante não encontrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma EntityNotFoundException quando o id da prova buscada não existir")
    void responderProva_LancaEntityNotFoundException_QuandoProvaIdNaoExistir(){
        doThrow(new EntityNotFoundException("Id da prova não existe"))
                .when(provaService).buscarPorId(ArgumentMatchers.anyLong());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(2L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("Id da prova não existe");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma TestErrorException quando a prova respondida não foi publicada ainda")
    void responderProva_LancaTestErrorException_QuandoProvaRespondidaNaoTiverSidoPublicada(){
        Prova prova = criarProva();
        prova.setPublicado(false);
        when(provaService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(prova);
        assertThatExceptionOfType(TestErrorException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("O tempo de prova já foi encerrado ou a prova não foi publicada ainda");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma TestErrorException quando a prova respondida já estiver expirada")
    void responderProva_LancaTestErrorException_QuandoProvaRespondidaJaEstiverExpirada(){
        Prova prova = criarProva();
        prova.setPublicado(true);
        prova.setTempoDeExpiracao(LocalDateTime.now().minusHours(2));
        when(provaService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(prova);
        assertThatExceptionOfType(TestErrorException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("O tempo de prova já foi encerrado ou a prova não foi publicada ainda");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma UserAlreadyBelongsToAnEntityException quando o estudante já tiver respondido a prova")
    void responderProva_LancaUserAlreadyBelongsToAnEntityException_QuandoEstudanteJaTiverRespondidoAProva(){
        when(respostaProvaRepository.findByEstudanteIdAndProvaId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(List.of(criarRespostaProva()));
        assertThatExceptionOfType(UserAlreadyBelongsToAnEntityException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("Estudante já respondeu a esta prova");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma EntityNotFoundException quando o id da questão respondida não existir")
    void responderProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistir(){
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("Id da questão não existe ou questão não pertence a esta prova");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("responderProva deve lançar uma EntityNotFoundException quando questão respondida não fizer parte da prova respondida")
    void responderProva_LancaEntityNotFoundException_QuandoQuestaoRespondidaNaoFizerParteDaProvaRespondida(){
        when(questaoRepository.existsByIdAndProvasId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(false);
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> respostaProvaService.responderProva(1L, criarRespostaProvaRequestDTO(), criarEstudante()))
                .withMessage("Id da questão não existe ou questão não pertence a esta prova");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("professor@example.com"),
                Mockito.eq("Envio de prova"), Mockito.eq("O aluno Ciclano enviou a prova da disciplina de Geografia, entre na plataforma para começar a correção!"));
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista de ProvaRespondidaResponseDTO quando a busca por provas respondidas é bem sucedida")
    void provasRespondidas_RetornaListaDeProvaRespondidaResponseDTO_QuandoABuscaPorProvasRespondidasEBemSucedida() {
        List<ProvaRespondidaResponseDTO> provaRespondidaResponseDTOS = respostaProvaService.provasRespondidas(criarProfessor(), 1L);
        assertThat(provaRespondidaResponseDTOS).isNotNull().isNotEmpty().hasSize(2);
        assertThat(provaRespondidaResponseDTOS.getFirst().getQuestoesRespondidas()).isNotNull().isNotEmpty().hasSize(2);
        assertThat(provaRespondidaResponseDTOS.getFirst().getNomeEstudante()).isEqualTo("Ciclano Sousa");
    }

    @Test
    @DisplayName("provasRespondidas deve retornar uma lista vazia quando não houverem provas respondidas")
    void provasRespondidas_RetornaListaVazia_QuandoNaoHouveremProvasRespondidas(){
        when(respostaProvaRepository.findAllByProvaIdAndRespondidaTrue(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());
        List<ProvaRespondidaResponseDTO> provaRespondidaResponseDTOS = respostaProvaService.provasRespondidas(criarEstudante(), 5L);
        assertThat(provaRespondidaResponseDTOS).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("provasRespondidas deve lançar uma EntityDoesntBelongToUserException quando o prova que deseja obter-se as respostas não pertencer ao professor")
    void provasRespondidas_RetornaEntityDoesntBelongToUserException_QuandoProvaNaoPertencerAoProfessor(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityDoesntBelongToUserException.class)
                .isThrownBy(() -> respostaProvaService.provasRespondidas(criarEstudante(), 2L))
                .withMessage("Prova não pertence a esse usuário");
    }

    @Test
    @DisplayName("salvarRespostasProva deve cadastrar as respostas da prova de um estudante quando bem sucedido")
    void salvarRespostasProva_CadastraRespostasDaProva_QuandoBemSucedido() {
        assertThatCode(() -> respostaProvaService.salvarRespostasProva(List.of(criarRespostaProva())))
                .doesNotThrowAnyException();
    }
}