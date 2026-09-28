package com.sistema_escolar.dtos.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class QuestaoRespondidaResponseDTO {
    private String pergunta;
    private String resposta;
}
