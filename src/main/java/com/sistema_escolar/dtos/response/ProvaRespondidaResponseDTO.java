package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class ProvaRespondidaResponseDTO {
    private Long estudanteId;
    private String nomeEstudante;
    private List<QuestaoRespondidaResponseDTO> questoesRespondidas;
}
