package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.QuestaoPostRequestDTO;
import com.sistema_escolar.dtos.response.ProvaAvaliadaResponseDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.EntityNotFoundException;
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
import com.sistema_escolar.utils.enums.UserRole;
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
    }

    @Test
    @DisplayName("criarProva deve retornar um ProvaResponseDTO quando a Prova e suas Questões forem cadastrados com sucesso")
    void criarProva_RetornaProvaResponseDTO_QuandoAProvaECadastradaComSucesso() {
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(criarProvaPostRequestDTO(), usuario);
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("criarProva deve retornar um ProvaResponseDTO e atribuir a criação da questão para o Professor que cadastrou ela quando criadoPor for null")
    void criarProva_RetornaProvaResponseDTOEAtribuiCriacaoDaQuestaoAoProfessor_QuandoAProvaECadastradaComSucessoECriadoPorENull(){
        ProvaPostRequestDTO provaPostRequestDTO = criarProvaPostRequestDTO();
        provaPostRequestDTO.getQuestoes().get(0).setCriadoPor(null);
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(provaPostRequestDTO, usuario);
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTO.getQuestoes().getFirst().getCriadoPor()).isEqualTo("professor@gmail.com");
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
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        ProvaResponseDTO provaResponseDTO = provaService.criarProva(provaPostRequestDTO, usuario);
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
        Usuario usuario = criarUsuario();
        usuario.setId(7L);
        usuario.setRole(UserRole.PROFESSOR);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.criarProva(criarProvaPostRequestDTO(), usuario))
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
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.criarProva(provaPostRequestDTO, usuario))
                .withMessage("Questão não existe");
    }

    @Test
    @DisplayName("criarProva deve lançar uma UserDoesntBelongException quando o professor não estiver vinculado a uma turma")
    void criarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverVinculadoAUmaTurma(){
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.criarProva(criarProvaPostRequestDTO(), usuario))
                .withMessage("Professor deve estar vinculado a uma turma para criar uma prova");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("atualizarProva deve retornar um ProvaResponseDTO quando a prova for atualizada com sucesso")
    void atualizarProva_RetornaProvaResponseDTO_QuandoProvaEAtualizadaComSucesso() {
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        ProvaResponseDTO provaResponseDTO = provaService.atualizarProva(1L, criarProvaPutRequestDTO(), usuario);
        assertThat(provaResponseDTO).isNotNull();
        assertThat(provaResponseDTO.getId()).isEqualTo(1L);
        assertThat(provaResponseDTO.getQuestoes()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(provaResponseDTO.getQuestoes().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando a prova não pertencer ao professor ou o id da prova não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.atualizarProva(5L, criarProvaPutRequestDTO(), usuario))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("atualizarProva deve lançar uma EntityNotFoundException quando o id de alguma questão não existir")
    void atualizarProva_LancaEntityNotFoundException_QuandoQuestaoIdNaoExistir(){
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.atualizarProva(5L, criarProvaPutRequestDTO(), usuario))
                .withMessage("Id da questão não existe");
        verify(provaRepository, times(0)).save(criarProva());
        verify(questaoRepository, times(0)).saveAll(List.of(criarQuestao()));
    }

    @Test
    @DisplayName("publicarProva deve publicar uma Prova quando bem sucedido")
    void publicarProva_PublicaUmaProva_QuandoBemSucedido() {
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        assertThatCode(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, usuario))
                .doesNotThrowAnyException();
        verify(mailService, times(1)).enviarEmail(Mockito.eq("ciclano@gmail.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserNotFoundException quando o id do Professor não existir")
    void publicarProva_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){
        doThrow(new UserNotFoundException("Professor não foi encontrado"))
                .when(professorService).buscarPorId(ArgumentMatchers.anyLong());
        Usuario usuario = criarUsuario();
        usuario.setId(7L);
        usuario.setRole(UserRole.PROFESSOR);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, usuario))
                .withMessage("Professor não foi encontrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@gmail.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma EntityNotFoundException quando a prova não pertencer ao professor ou id da prova não existir")
    void publicarProva_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 5L, usuario))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@gmail.com"),
                Mockito.eq("Postagem de prova"), Mockito.contains("Olá, Ciclano, uma nova prova foi postada"));
    }

    @Test
    @DisplayName("publicarProva deve lançar uma UserDoesntBelongException quando professor não estiver vinculado a turma")
    void publicarProva_LancaUserDoesntBelongException_QuandoProfessorNaoEstiverVinculadoATurma(){
        when(turmaRepository.findByProfessorId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.PROFESSOR);
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.publicarProva(criarPublishProvaRequestDTO(), 1L, usuario))
                .withMessage("Professor não está vinculado a uma turma");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@gmail.com"),
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
        Prova prova = provaService.buscarPorIdEEmailDoProfessor(1L, "professor@gmail.com");
        assertThat(prova).isNotNull();
        assertThat(prova.getId()).isEqualTo(1L);
        assertThat(prova.getEmailProfessor()).isEqualTo("professor@gmail.com");
    }

    @Test
    @DisplayName("buscarPorIdEEmailDoProfessor deve lançar uma EntityNotFoundException quando id da prova não existir ou ela não estiver vinculada ao professor")
    void buscarPorIdEEmailDoProfessor_LancaEntityNotFoundException_QuandoProvaNaoPertenceAoProfessorOuProvaIdNaoExistir(){
        when(provaRepository.findByIdAndEmailProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.buscarPorIdEEmailDoProfessor(5L, "fulano@gmail.com"))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
    }

    @Test
    @DisplayName("getProvaAvaliada deve retornar uma ProvaAvaliadaResponseDTO quando a busca por uma prova avaliada é bem sucedida")
    void getProvaAvaliada_RetornaProvaAvaliadaResponseDTO_QuandoABuscaPorProvaAvaliadaEBemSucedida() {
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        ProvaAvaliadaResponseDTO provaAvaliada = provaService.getProvaAvaliada(1L, usuario);
        assertThat(provaAvaliada).isNotNull();
        assertThat(provaAvaliada.getProvaId()).isEqualTo(1L);
        assertThat(provaAvaliada.getQuestoesAvaliadas()).isNotEmpty().isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("getProvaAvaliada deve lançar uma UserNotFoundException quando o id do estudante passado não existir")
    void getProvaAvaliada_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(estudanteService).buscarPorId(ArgumentMatchers.anyLong());
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        usuario.setId(7L);
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> provaService.getProvaAvaliada(1L, usuario))
                .withMessage("Estudante não encontrado");
    }

    @Test
    @DisplayName("getProvaAvaliada deve lançar uma EntityNotFoundException quando o id da prova buscada não existir")
    void getProvaAvaliada_LancaEntityNotFoundException_QuandoProvaIdNaoExistir(){
        when(provaRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> provaService.getProvaAvaliada(5L, usuario))
                .withMessage("Id da prova não existe");
    }

    @Test
    @DisplayName("getProvaAvaliada deve lançar uma UserDoesntBelongException quando não houverem respostas do estudante para a prova buscada")
    void getProvaAvaliada_LancaUserDoesntBelongException_QuandoNaoHouveremRespostasDoEstudanteParaAProva(){
        when(respostaProvaRepository.findByEstudanteIdAndProvaIdAndAvaliadaTrue(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());
        Usuario usuario = criarUsuario();
        usuario.setRole(UserRole.ESTUDANTE);
        assertThatExceptionOfType(UserDoesntBelongException.class)
                .isThrownBy(() -> provaService.getProvaAvaliada(1L, usuario))
                .withMessage("Estudante não fez esta prova");
    }
}