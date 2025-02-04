package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.QuestaoPostRequestDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Professor;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.TestErrorException;
import com.sistema_escolar.exceptions.UserDoesntBelongException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.ProvaRepository;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.repositories.RespostaProvaRepository;
import com.sistema_escolar.repositories.TurmaRepository;
import com.sistema_escolar.services.EstudanteService;
import com.sistema_escolar.services.MailService;
import com.sistema_escolar.services.ProfessorService;
import com.sistema_escolar.services.ProvaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class ProvaServiceTest {

    @InjectMocks
    private ProvaService provaService;

    @Mock
    private ProvaRepository provaRepository;

    @Mock
    private ProfessorService professorService;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private RespostaProvaRepository respostaProvaRepository;

    @Mock
    private MailService mailService;

    @Mock
    private EstudanteService estudanteService;

    @BeforeEach
    void setup(){
        when(professorService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarProfessor());
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarTurma()));
        when(provaRepository.save(ArgumentMatchers.any(Prova.class)))
                .thenReturn(criarProva());
        when(questaoRepository.saveAll(ArgumentMatchers.anyList()))
                .thenReturn(List.of(criarQuestao()));
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(criarProva()));
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarQuestao()));
        when(provaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarProva()));
        when(estudanteService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarEstudante());
        when(respostaProvaRepository.findByEstudanteIdAndProvaIdAndAvaliadaTrue(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(List.of(criarRespostaProva()));
        when(provaRepository.existsByIdAndPublicadoTrue(ArgumentMatchers.anyLong()))
                .thenReturn(false);
    }

    @Test
    @DisplayName("criarProva deve retornar um ProvaResponseDTO quando a Prova e suas Questões forem cadastrados com sucesso")
    void criarProva_RetornaProvaResponseDTO_QuandoAProvaECadastradaComSucesso() {
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(criarProvaPostRequestDTO(), criarProfessor());
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("criarProva deve retornar um ProvaResponseDTO e atribuir a criação da questão para o Professor que cadastrou ela quando criadoPor for null")
    void criarProva_RetornaProvaResponseDTOEAtribuiCriacaoDaQuestaoAoProfessor_QuandoAProvaECadastradaComSucessoECriadoPorENull(){
        ProvaPostRequestDTO provaPostRequestDTO = criarProvaPostRequestDTO();
        provaPostRequestDTO.getQuestoes().get(0).setCriadoPor(null);
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(provaPostRequestDTO, criarProfessor());
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTO.getQuestoes().getFirst().getCriadoPor()).isEqualTo("professor@example.com");
    }

    @Test
    @DisplayName("criarProva deve adicionar uma prova a lista de provas que utilizam a questão quando id da questão existir e retornar ProvaResponseDTO quando o cadastro for bem sucedido")
    void criarProva_AdicionaProvaALIstaDeProvasQueUtilizamQuestaoERetornaProvaResponseDTO_QuandoQuestaoIdExistirECadastroForBemSucedido(){
        Prova prova = criarProva();
        prova.setQuestoes(null);
        Questao questao = criarQuestao();
        questao.setProvas(List.of(prova));
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(questao));
        QuestaoPostRequestDTO questaoPostRequestDTO = criarQuestaoPostRequestDTO();
        questaoPostRequestDTO.setId(1L);
        ProvaPostRequestDTO provaPostRequestDTO = criarProvaPostRequestDTO();
        provaPostRequestDTO.setQuestoes(List.of(questaoPostRequestDTO));
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(provaPostRequestDTO, criarProfessor());
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTO.getQuestoes().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("criarProva deve lançar uma UserNotFoundException quando o id do Professor não existir")
    void criarProva_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){

        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(professorService).buscarPorId(ArgumentMatchers.anyLong());
        Professor professor = criarProfessor();
        professor.setId(7L);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.criarProva(criarProvaPostRequestDTO(), professor))
                .withMessage("Professor não foi encontrado");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("criarProva deve lançar uma EntityNotFoundException quando o id da questão passada não existir")
    void criarProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistir(){
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        QuestaoPostRequestDTO questaoPostRequestDTO = criarQuestaoPostRequestDTO();
        questaoPostRequestDTO.setId(4L);
        ProvaPostRequestDTO provaPostRequestDTO = criarProvaPostRequestDTO();
        provaPostRequestDTO.setQuestoes(List.of(questaoPostRequestDTO));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.criarProva(provaPostRequestDTO, criarProfessor()))
                .withMessage("Questão não existe");
    }

    @Test
    @DisplayName("criarProva deve lançar uma UserDoesntBelongException quando o professor não estiver vinculado a uma turma")
    void criarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverVinculadoAUmaTurma(){
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.criarProva(criarProvaPostRequestDTO(), criarProfessor()))
                .withMessage("Professor deve estar vinculado a uma turma para criar uma prova");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("atualizarProva deve retornar um ProvaResponseDTO quando a prova for atualizada com sucesso")
    void atualizarProva_RetornaProvaResponseDTO_QuandoProvaEAtualizadaComSucesso() {
        ProvaResponseDTO provaResponseDTO = provaService.atualizarProva(1L, criarProvaPutRequestDTO(), criarProfessor());
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTO.getQuestoes().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando a prova não pertencer ao professor ou o id da prova não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.atualizarProva(5L, criarProvaPutRequestDTO(), criarProfessor()))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando o id de alguma questão não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistir(){
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.atualizarProva(5L, criarProvaPutRequestDTO(), criarProfessor()))
                .withMessage("Id da questão não existe");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("publicarProva deve publicar uma Prova quando bem sucedido")
    void publicarProva_PublicaUmaProva_QuandoBemSucedido() {
        assertThatCode(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, criarProfessor()))
                .doesNotThrowAnyException();
        verify(mailService, times(1)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserNotFoundException quando o id do Professor não existir")
    void publicarProva_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(professorService).buscarPorId(ArgumentMatchers.anyLong());
        Professor professor = criarProfessor();
        professor.setId(7L);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, professor))
                .withMessage("Professor não foi encontrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma EntityNotFoundException quando a prova não pertencer ao professor ou id da prova não existir")
    void publicarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 5L, criarProfessor()))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserDoesntBelongException quando professor não estiver vinculado a turma")
    void publicarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverVinculadoATurma(){
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, criarProfessor()))
                .withMessage("Professor não está vinculado a uma turma");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma TestErrorException quando professor tentar publicar uma prova que já está publicada")
    void publicarProva_LancaTestErrorException_QuandoProvaJaEstaPublicada(){
        when(provaRepository.existsByIdAndPublicadoTrue(ArgumentMatchers.anyLong()))
                .thenReturn(true);
        assertThatExceptionOfType(TestErrorException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, criarProfessor()))
                .withMessage("Prova já foi publicada");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("buscarPorId deve retornar uma Prova quando o id buscado existir")
    void buscarPorId_RetornaProva_QuandoIdBuscadoExistir() {
        Prova prova = provaService.buscarPorId(1L);
        assertThat(prova).isNotNull();
        assertThat(prova.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorId deve lançar uma EntityNotFoundException quando o id buscado não existir")
    void buscarPorId_LancaEntityNotFoundException_QuandoIdBuscadoNaoExistir(){
        when(provaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.buscarPorId(5L))
                .withMessage("Id da prova não existe");
    }

    @Test
    @DisplayName("buscarPorIdEEmailDoProfessor deve retornar uma prova quando o id da prova buscado existir e ela estiver vinculada ao professor")
    void buscarPorIdEEmailDoProfessor_RetornaProva_QuandoProvaExistirEEstiverVinculadaAoProfessor() {
        Prova prova = provaService.buscarPorIdEEmailDoProfessor(1L, "professor@example.com");
        assertThat(prova).isNotNull();
        assertThat(prova.getId()).isEqualTo(1L);
        assertThat(prova.getEmailProfessor()).isEqualTo("professor@example.com");
    }

    @Test
    @DisplayName("buscarPorIdEEmailDoProfessor deve lançar uma EntityNotFoundException quando id da prova não existir ou ela não estiver vinculada ao professor")
    void buscarPorIdEEmailDoProfessor_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.buscarPorIdEEmailDoProfessor(5L, "fulano@example.com"))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve retornar uma ProvaAvaliadaResponseDTO quando a busca por uma prova avaliada é bem sucedida")
    void buscarProvaAvaliada_RetornaProvaAvaliadaResponseDTO_QuandoABuscaPorProvaAvaliadaEBemSucedida() {
        ProvaAvaliadaResponseDTO provaAvaliada = provaService.buscarProvaAvaliada(1L, criarProfessor());
        assertThat(provaAvaliada).isNotNull();
        assertThat(provaAvaliada.getProvaId()).isEqualTo(1L);
        assertThat(provaAvaliada.getQuestoesAvaliadas()).isNotEmpty().isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma UserNotFoundException quando o id do estudante passado não existir")
    void buscarProvaAvaliada_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(estudanteService).buscarPorId(ArgumentMatchers.anyLong());
        Professor professor = criarProfessor();
        professor.setId(7L);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.buscarProvaAvaliada(1L, professor))
                .withMessage("Estudante não encontrado");
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma EntityNotFoundException quando o id da prova buscada não existir")
    void buscarProvaAvaliada_LancaEntityNotFoundException_QuandoProvaIdNaoExistir(){
        when(provaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.buscarProvaAvaliada(5L, criarProfessor()))
                .withMessage("Id da prova não existe");
    }

    @Test
    @DisplayName("buscarProvaAvaliada deve lançar uma UserDoesntBelongException quando não houverem respostas do estudante para a prova buscada")
    void buscarProvaAvaliada_LancaUserDoesntBelongException_QuandoNaoHouveremRespostasDoEstudanteParaAProva(){
        when(respostaProvaRepository.findByEstudanteIdAndProvaIdAndAvaliadaTrue(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.buscarProvaAvaliada(1L, criarProfessor()))
                .withMessage("Estudante não fez esta prova");
    }
}