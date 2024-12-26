package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EstatisticasTurmasResponseDTO {
    private Long turmaId;
    private long qtdEstudantes;
}
