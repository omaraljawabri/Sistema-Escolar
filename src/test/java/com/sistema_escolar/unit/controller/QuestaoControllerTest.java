package com.sistema_escolar.unit.controller;

import com.sistema_escolar.controllers.QuestaoController;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.Usuario;
import com.sistema_escolar.services.QuestaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class QuestaoControllerTest {

    @InjectMocks
    private QuestaoController questaoController;

    @Mock
    private QuestaoService questaoService;

    @BeforeEach
    void setup(){
        PageImpl<QuestaoResponseDTO> questaoResponseDTOS = new PageImpl<>(List.of(criarQuestaoResponseDTO()));
        when(questaoService.buscarQuestoes(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
                .thenReturn(questaoResponseDTOS);
    }

    @Test
    @DisplayName("buscarQuestoes deve retornar uma lista paginada de QuestaoResponseDTO quando a busca for bem sucedida")
    void buscarQuestoes_RetornaListaPaginadaDeQuestaoResponseDTO_QuandoBemSucedido() {
        mockAuthentication();
        ResponseEntity<Page<QuestaoResponseDTO>> pageResponseEntity =
                questaoController.buscarQuestoes(0, 1);
        assertThat(pageResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pageResponseEntity.getBody()).isNotNull();
        assertThat(pageResponseEntity.getBody().getContent()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(pageResponseEntity.getBody().getContent().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarQuestoes deve retornar uma lista paginada vazia quando não houverem questões cadastradas no banco de dados")
    void buscarQuestoes_RetornaListaPaginadaVazia_QuandoNaoHaQuestoesCadastradas(){
        mockAuthentication();
        when(questaoService.buscarQuestoes(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
                .thenReturn(Page.empty());
        ResponseEntity<Page<QuestaoResponseDTO>> pageResponseEntity = questaoController.buscarQuestoes(0, 1);
        assertThat(pageResponseEntity.getStatusCode()).isNotNull();
        assertThat(pageResponseEntity.getBody()).isNotNull();
        assertThat(pageResponseEntity.getBody().getContent()).isNotNull().isEmpty();
    }
}