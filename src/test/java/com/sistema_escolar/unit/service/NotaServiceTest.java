package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.request.NotaRequestDTO;
import com.sistema_escolar.dtos.response.NotaResponseDTO;
import com.sistema_escolar.entities.Nota;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.exceptions.EntityNotFoundException;
import com.sistema_escolar.exceptions.QuestionErrorException;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.EstudanteRepository;
import com.sistema_escolar.repositories.NotaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class NotaServiceTest {

    @InjectMocks
    private NotaService notaService;

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private ProfessorService professorService;

    @Mock
    private ProvaService provaService;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private RespostaProvaRepository respostaProvaRepository;

    @Mock
    private RespostaProvaService respostaProvaService;

    @Mock
    private EstudanteService estudanteService;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setup(){
        when(estudanteService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarEstudante());
        when(professorService.buscarPorId(ArgumentMatchers.anyLong()))
                .thenReturn(criarProfessor());
        when(provaService.buscarPorIdEEmailDoProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(criarProva());
        when(notaRepository.save(ArgumentMatchers.any(Nota.class)))
                .thenReturn(criarNota());
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarQuestao()));
        when(respostaProvaRepository.findByQuestaoIdAndProvaIdAndEstudanteId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarRespostaProva()));
    }

    @Test
    @DisplayName("avaliarProva deve retornar um NotaResponseDTO quando a prova for avaliada com sucesso")
    void avaliarProva_RetornaNotaResponseDTO_QuandoProvaEAvaliadaComSucesso() {
        NotaResponseDTO notaResponseDTO = notaService.avaliarProva(1L, List.of(criarNotaRequestDTO()), criarUsuario(), 1L);
        assertThat(notaResponseDTO).isNotNull();
        assertThat(notaResponseDTO.getNotaProva()).isEqualTo(2D);
        verify(mailService, times(1)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma UserNotFoundException quando o Id do estudante passado não existir")
    void avaliarProva_LancaUserNotFoundException_QuandoEstudanteIdNaoExistir(){
        doThrow(new UserNotFoundException("Estudante não encontrado"))
                .when(estudanteService).buscarPorId(ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> notaService.avaliarProva(1L, List.of(criarNotaRequestDTO()), criarUsuario(), 2L))
                .withMessage("Estudante não encontrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma UserNotFoundException quando o Id do professor passado não existir")
    void avaliarProva_LancaUserNotFoundException_QuandoProfessorIdNaoExistir(){
        doThrow(new UserNotFoundException("Professor não encontrado"))
                .when(professorService).buscarPorId(ArgumentMatchers.anyLong());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> notaService.avaliarProva(1L, List.of(criarNotaRequestDTO()), criarUsuario(), 2L))
                .withMessage("Professor não encontrado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma EntityNotFoundException quando Id da prova e/ou Email do professor não existir")
    void avaliarProva_LancaEntityNotFoundException_QuandoProvaIdEOuEmailProfessorNaoExistir(){
        doThrow(new EntityNotFoundException("Prova não pertence a esse professor ou id da prova não existe"))
                .when(provaService).buscarPorIdEEmailDoProfessor(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> notaService.avaliarProva(2L, List.of(criarNotaRequestDTO()), criarUsuario(), 2L))
                .withMessage("Prova não pertence a esse professor ou id da prova não existe");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma QuestionErrorException quando a nota atribuida a questão for maior do que seu valor máximo")
    void avaliarProva_LancaQuestionErrorException_QuandoNotaDaQuestaoForMaiorQueSeuValorMaximo(){
        NotaRequestDTO notaRequestDTO = criarNotaRequestDTO();
        notaRequestDTO.setNotaQuestao(8D);
        assertThatExceptionOfType(QuestionErrorException.class)
                .isThrownBy(() -> notaService.avaliarProva(1L, List.of(notaRequestDTO), criarUsuario(), 1L))
                .withMessage("Nota da questão é maior do que seu valor máximo");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma EntityNotFoundException quando o id da questão avaliada não existir")
    void avaliarProva_LancaEntityNotFoundException_QuandoIdDaQuestaoNaoExistir(){
        NotaRequestDTO notaRequestDTO = criarNotaRequestDTO();
        notaRequestDTO.setQuestaoId(6L);
        when(questaoRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> notaService.avaliarProva(1L, List.of(notaRequestDTO), criarUsuario(), 1L))
                .withMessage("Id da questão não existe");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }

    @Test
    @DisplayName("avaliarProva deve lançar uma QuestionErrorException quando a questão passada não tiver sido respondida pelo estudante passado")
    void avaliarProva_LancaQuestionErrorException_QuandoQuestaoNaoTiverSidoRespondidaPeloEstudante(){
        when(respostaProvaRepository.findByQuestaoIdAndProvaIdAndEstudanteId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(QuestionErrorException.class)
                .isThrownBy(() -> notaService.avaliarProva(5L, List.of(criarNotaRequestDTO()), criarUsuario(), 2L))
                .withMessage("Questão não foi respondida pelo estudante na prova com id passado");
        verify(mailService, times(0)).enviarEmail(Mockito.eq("ciclano@example.com"),
                Mockito.eq("Recebimento de nota"), Mockito.contains("Olá, uma nova nota sua foi publicada na disciplina"));
    }
}