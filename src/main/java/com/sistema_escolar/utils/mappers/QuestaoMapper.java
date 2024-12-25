package com.sistema_escolar.utils.mappers;

import com.sistema_escolar.dtos.response.QuestaoPutResponseDTO;
import com.sistema_escolar.dtos.response.QuestaoResponseDTO;
import com.sistema_escolar.entities.Questao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public abstract class QuestaoMapper {
    public static final QuestaoMapper INSTANCE = Mappers.getMapper(QuestaoMapper.class);
    public abstract QuestaoResponseDTO toQuestaoResponseDTO(Questao questoes);
}
