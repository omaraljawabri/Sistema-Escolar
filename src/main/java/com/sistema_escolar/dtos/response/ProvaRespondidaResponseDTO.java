package com.sistema_escolar.dtos.response;

import lombok.*;

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
