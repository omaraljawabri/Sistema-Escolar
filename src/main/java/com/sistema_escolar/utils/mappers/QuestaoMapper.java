package com.sistema_escolar.utils.mappers;

import com.sistema_escolar.dtos.request.QuestaoPostRequestDTO;
import com.sistema_escolar.dtos.request.QuestaoPutRequestDTO;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.Questao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class QuestaoMapper {
    public static final QuestaoMapper INSTANCE = Mappers.getMapper(QuestaoMapper.class);
    public abstract Questao toQuestao(QuestaoPostRequestDTO questaoPostRequestDTO);
    public abstract Questao toQuestao(QuestaoPutRequestDTO questaoPutRequestDTO);
    public abstract QuestaoResponseDTO toQuestaoResponseDTO(Questao questoes);
}
