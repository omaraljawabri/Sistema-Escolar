package com.sistema_escolar.utils.mappers;

import com.sistema_escolar.dtos.request.ProvaPostRequestDTO;
import com.sistema_escolar.dtos.request.ProvaPutRequestDTO;
import com.sistema_escolar.dtos.response.ProvaResponseDTO;
import com.sistema_escolar.entities.Prova;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class ProvaMapper {
    public static final ProvaMapper INSTANCE = Mappers.getMapper(ProvaMapper.class);
    public abstract Prova toProva(ProvaPutRequestDTO provaPutRequestDTO);
    public abstract Prova toProva(ProvaPostRequestDTO provaPostRequestDTO);
    public abstract ProvaResponseDTO toProvaResponseDTO(Prova prova);
}
