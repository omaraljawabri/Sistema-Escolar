package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EstatisticasDisciplinasResponseDTO {
    private Long disciplinaId;
    private long qtdEstudantes;
    private long qtdTurmas;
}
