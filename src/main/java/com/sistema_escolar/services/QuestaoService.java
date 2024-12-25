package com.sistema_escolar.services;

import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.repositories.QuestaoRepository;
import com.sistema_escolar.utils.mappers.QuestaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepository;

    public Page<QuestaoResponseDTO> findQuestoes(int pagina, int quantidade) {
        return questaoRepository.findAll(PageRequest.of(pagina, quantidade)).map(QuestaoMapper.INSTANCE::toQuestaoResponseDTO);
    }
}
