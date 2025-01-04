package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.response.EstatisticasEstudanteResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasGeraisResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.entities.Prova;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.repositories.*;
import com.sistema_escolar.services.EstatisticasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class EstatisticasServiceTest {

    @InjectMocks
    private EstatisticasService estatisticasService;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private ProvaRepository provaRepository;

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private RespostaProvaRepository respostaProvaRepository;

    @BeforeEach
    void setup(){
        when(turmaRepository.findByIdAndProfessorId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarTurma()));
        Prova prova = criarProva();
        prova.setTempoDeExpiracao(LocalDateTime.now().minusHours(2));
        when(provaRepository.findByDisciplinaIdAndEmailProfessorAndPublicadoTrue(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(List.of(prova));
        when(notaRepository.findByEstudanteIdAndProvaId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(criarNota()));
        when(notaRepository.findAllByEstudanteId(ArgumentMatchers.anyLong()))
                .thenReturn(List.of(criarNota()));
        when(provaRepository.findByNotas(ArgumentMatchers.anyList()))
                .thenReturn(List.of(criarProva()));
        when(disciplinaRepository.count())
                .thenReturn(1L);
        when(estudanteRepository.count())
                .thenReturn(1L);
        when(turmaRepository.count())
                .thenReturn(1L);
        when(disciplinaRepository.findAll())
                .thenReturn(List.of(criarDisciplina()));
        when(estudanteRepository.countByDisciplinasId(ArgumentMatchers.anyLong()))
                .thenReturn(1L);
        when(estudanteRepository.countByDisciplinasId(ArgumentMatchers.anyLong()))
                .thenReturn(1L);
        when(turmaRepository.countByDisciplinaId(ArgumentMatchers.anyLong()))
                .thenReturn(1L);
        when(turmaRepository.findAll())
                .thenReturn(List.of(criarTurma()));
        when(estudanteRepository.countByTurmasId(ArgumentMatchers.anyLong()))
                .thenReturn(1L);
        when(respostaProvaRepository.existsByProvaIdAndEstudanteId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(true);
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar uma EstatisticasTurmaResponseDTO quando a busca por estatísticas for bem sucedida")
    void estatisticasDaTurma_RetornaEstatisticasTurmaResponseDTO_QuandoABuscaPorEstatisticasEBemSucedida() {
        EstatisticasTurmaResponseDTO estatisticasTurmaResponseDTO = estatisticasService.estatisticasDaTurma(1L, criarProfessor());
        assertThat(estatisticasTurmaResponseDTO).isNotNull();
        assertThat(estatisticasTurmaResponseDTO.getEstatisticasProva()).isNotEmpty().isNotNull().hasSize(1);
        assertThat(estatisticasTurmaResponseDTO.getMediaGeral()).isEqualTo(BigDecimal.valueOf(10D));
    }

    @Test
    @DisplayName("estatisticasDaTurma deve lançar uma UserNotFoundException quando a turma buscada pelo seu id e id do professor não existir")
    void estatisticasDaTurma_LancaUserNotFoundException_QuandoTurmaNaoExistirPelaTurmaIdEProfessorId(){
        when(turmaRepository.findByIdAndProfessorId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> estatisticasService.estatisticasDaTurma(2L, criarProfessor()))
                .withMessage("Professor não faz parte dessa turma ou turma não existe");
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar 0 como valor dos atributos quando não houverem provas publicadas ou expiradas daquela turma")
    void estatisticasDaTurma_RetornaZeroComoValores_QuandoNaoHouveremProvasPublicadasOuExpiradasNaTurma(){
        when(provaRepository.findByDisciplinaIdAndEmailProfessorAndPublicadoTrue(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());
        EstatisticasTurmaResponseDTO estatisticasTurmaResponseDTO = estatisticasService.estatisticasDaTurma(1L, criarProfessor());
        assertThat(estatisticasTurmaResponseDTO).isNotNull();
        assertThat(estatisticasTurmaResponseDTO.getMediaGeral()).isEqualTo(BigDecimal.ZERO);
        assertThat(estatisticasTurmaResponseDTO.getPorcentagemAprovados()).isEqualTo(BigDecimal.ZERO);
        assertThat(estatisticasTurmaResponseDTO.getEstatisticasProva()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("estatisticasDoEstudante deve retornar uma EstatisticasEstudanteResponseDTO quando a busca por estatísticas do estudante for bem sucedida")
    void estatisticasDoEstudante_RetornaEstatisticasEstudanteResponseDTO_QuandoABuscaPorEstatisticasEBemSucedida() {
        EstatisticasEstudanteResponseDTO estatisticasEstudanteResponseDTO = estatisticasService.estatisticasDoEstudante(criarEstudante());
        assertThat(estatisticasEstudanteResponseDTO).isNotNull();
        assertThat(estatisticasEstudanteResponseDTO.getEstatisticasPorProva()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasEstudanteResponseDTO.getMediaGeral()).isEqualTo(BigDecimal.valueOf(10D));
        assertThat(estatisticasEstudanteResponseDTO.getEstatisticasPorProva().getFirst().getProvaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("estatisticasDoEstudante deve retornar valores zerados quando a busca por estatísticas do estudante não retornar resultados")
    void estatisticasDoEstudante_RetornaZeroComoValores_QuandoNaoHouveremRegistros(){
        when(notaRepository.findAllByEstudanteId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());
        when(provaRepository.findByNotas(ArgumentMatchers.anyList()))
                .thenReturn(Collections.emptyList());
        EstatisticasEstudanteResponseDTO estatisticasEstudanteResponseDTO = estatisticasService.estatisticasDoEstudante(criarEstudante());
        assertThat(estatisticasEstudanteResponseDTO).isNotNull();
        assertThat(estatisticasEstudanteResponseDTO.getEstatisticasPorProva()).isNotNull().isEmpty();
        assertThat(estatisticasEstudanteResponseDTO.getMediaGeral()).isEqualTo(BigDecimal.valueOf(0D));
    }

    @Test
    @DisplayName("estatisticasGerais retorna uma EstatisticasGeraisResponseDTO quando a busca por estatísticas gerais do sistema é bem sucedida")
    void estatisticasGerais_RetornaEstatisticasGeraisResponseDTO_QuandoABuscaPorEstatisticasEBemSucedida() {
        EstatisticasGeraisResponseDTO estatisticasGeraisResponseDTO = estatisticasService.estatisticasGerais();
        assertThat(estatisticasGeraisResponseDTO).isNotNull();
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasDisciplinas()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasTurmas()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasGeraisResponseDTO.getQtdDisciplinasGeral()).isEqualTo(1L);
    }

    @Test
    @DisplayName("estatisticasGerais retorna uma EstatisticasGeraisResponseDTO com lista de EstatisticasTurmasResponseDTO vazia quando não há dados sobre as turmas do sistema")
    void estatisticasGerais_RetornaEstatisticasGeraisResponseDTOComListaDeEstatisticasTurmasResponseDTOVazia_QuandoNaoHaRegistrosDeTurmasNoSistema(){
        when(turmaRepository.findAll())
                .thenReturn(Collections.emptyList());
        EstatisticasGeraisResponseDTO estatisticasGeraisResponseDTO = estatisticasService.estatisticasGerais();
        assertThat(estatisticasGeraisResponseDTO).isNotNull();
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasDisciplinas()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasTurmas()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("estatisticasGerais retorna uma EstatisticasGeraisResponseDTO com lista de EstatisticasDisciplinasResponseDTO e EstatisticasTurmasResponseDTO vazia quando não há dados sobre as disciplinas do sistema")
    void estatisticasGerais_RetornaEstatisticasGeraisResponseDTOComListaDeEstatisticasDisciplinasResponseDTOEEstatisticasTurmasResponseDTOVazia_QuandoNaoHaRegistrosDeDisciplinasNoSistema(){
        when(disciplinaRepository.findAll())
                .thenReturn(Collections.emptyList());
        when(turmaRepository.findAll())
                .thenReturn(Collections.emptyList());
        EstatisticasGeraisResponseDTO estatisticasGeraisResponseDTO = estatisticasService.estatisticasGerais();
        assertThat(estatisticasGeraisResponseDTO).isNotNull();
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasTurmas()).isNotNull().isEmpty();
        assertThat(estatisticasGeraisResponseDTO.getEstatisticasDisciplinas()).isNotNull().isEmpty();
    }
}