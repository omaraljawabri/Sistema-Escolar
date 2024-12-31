package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.EstatisticasController;
import com.sistema_escolar.dtos.response.EstatisticasEstudanteResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasGeraisResponseDTO;
import com.sistema_escolar.dtos.response.EstatisticasTurmaResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.exceptions.UserNotFoundException;
import com.sistema_escolar.services.EstatisticasService;
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class EstatisticasControllerTest {

    @InjectMocks
    private EstatisticasController estatisticasController;

    @Mock
    private EstatisticasService estatisticasService;

    @BeforeEach
    void setup(){
        when(estatisticasService.estatisticasDoEstudante(ArgumentMatchers.any(Usuario.class)))
                .thenReturn(criarEstatisticasEstudanteResponseDTO());
        when(estatisticasService.estatisticasGerais())
                .thenReturn(criarEstatisticasGeraisResponseDTO());
        when(estatisticasService.estatisticasDaTurma(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class)))
                .thenReturn(criarEstatisticasTurmaResponseDTO());
    }

    @Test
    @DisplayName("estatisticasDoEstudante deve retornar um EstatisticasEstudanteResponseDTO quando a busca por estatisticas do Estudante for bem sucedida")
    void estatisticasDoEstudante_RetornaEstatisticasEstudanteResponseDTO_QuandoABuscaPorEstatisticasDoEstudanteEBemSucedida() {
        mockAuthentication();
        ResponseEntity<EstatisticasEstudanteResponseDTO> estatisticasEstudanteResponseDTOResponseEntity =
                estatisticasController.estatisticasDoEstudante();
        assertThat(estatisticasEstudanteResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(estatisticasEstudanteResponseDTOResponseEntity.getBody().getMediaGeral()).isEqualTo(BigDecimal.TEN);
        assertThat(estatisticasEstudanteResponseDTOResponseEntity.getBody().getEstatisticasPorProva()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasEstudanteResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("estatisticasGerais deve retornar um EstatisticasGeraisResponseDTO quando a busca por estatísticas gerais do sistema for bem sucedida")
    void estatisticasGerais_RetornaEstatisticasGeraisResponseDTO_QuandoABuscaPorEstatisticasGeraisDoSistemaEBemSucedida() {
        mockAuthentication();
        ResponseEntity<EstatisticasGeraisResponseDTO> estatisticasGeraisResponseDTOResponseEntity = estatisticasController.estatisticasGerais();
        assertThat(estatisticasGeraisResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estatisticasGeraisResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(estatisticasGeraisResponseDTOResponseEntity.getBody().getQtdDisciplinasGeral()).isEqualTo(1L);
        assertThat(estatisticasGeraisResponseDTOResponseEntity.getBody().getEstatisticasDisciplinas()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(estatisticasGeraisResponseDTOResponseEntity.getBody().getEstatisticasTurmas()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("estatisticasDaTurma deve retornar um EstatisticasTurmaResponseDTO quando a busca por estatísticas de uma turma for bem sucedida")
    void estatisticasDaTurma_RetornaEstatisticasTurmaResponseDTO_QuandoABuscaPorEstatisticasDeUmaTurmaEBemSucedida(){
        mockAuthentication();
        ResponseEntity<EstatisticasTurmaResponseDTO> estatisticasTurmaResponseDTOResponseEntity = estatisticasController.estatisticasDaTurma(1L);
        assertThat(estatisticasTurmaResponseDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estatisticasTurmaResponseDTOResponseEntity.getBody()).isNotNull();
        assertThat(estatisticasTurmaResponseDTOResponseEntity.getBody().getPorcentagemAprovados()).isEqualTo(BigDecimal.valueOf(100D));
        assertThat(estatisticasTurmaResponseDTOResponseEntity.getBody().getEstatisticasProva()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("estatisticasDaTurma deve lançar uma UserNotFoundException quando professor não fizer parte da turma com id passado ou id da turma não existir")
    void estatisticasDaTurma_LancaUserNotFoundException_QuandoProfessorNaoFazParteDaTurmaOuTurmaNaoExiste(){
        mockAuthentication();
        doThrow(new UserNotFoundException("Professor não faz parte dessa turma ou turma não existe"))
                .when(estatisticasService).estatisticasDaTurma(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Usuario.class));
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> estatisticasController.estatisticasDaTurma(1L))
                .withMessage("Professor não faz parte dessa turma ou turma não existe");
    }
}