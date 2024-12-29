package com.sistema_escolar.unit.service;

import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.Questao;
import com.sistema_escolar.repositories.QuestaoRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static com.sistema_escolar.utils.EntityUtils.*;

@ExtendWith(SpringExtension.class)
class QuestaoServiceTest {

    @InjectMocks
    private QuestaoService questaoService;

    @Mock
    private QuestaoRepository questaoRepository;

    @BeforeEach
    void setup(){
        PageImpl<Questao> questaoPage = new PageImpl<>(List.of(criarQuestao()));
        when(questaoRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(questaoPage);
    }

    @Test
    @DisplayName("buscarQuestoes retorna uma lista de questões paginada quando bem sucedido")
    void buscarQuestoes_RetornaQuestoesPaginadas_QuandoBemSucedido() {
        Page<QuestaoResponseDTO> questaoResponseDTOS = questaoService.buscarQuestoes(0, 1);
        assertThat(questaoResponseDTOS.toList()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(questaoResponseDTOS.toList().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarQuestoes retorna uma lista paginada vazia quando não há questões registradas")
    void buscarQuestoes_RetornaListaPaginadaVazia_QuandoNaoHaRegistros(){
        when(questaoRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Page.empty());
        Page<QuestaoResponseDTO> questaoResponseDTOS = questaoService.buscarQuestoes(0, 1);
        assertThat(questaoResponseDTOS.toList()).isNotNull().isEmpty();
    }
}