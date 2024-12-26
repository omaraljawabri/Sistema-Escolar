package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EstatisticasEstudanteResponseDTO {
    private BigDecimal mediaGeral;
    private BigDecimal porcentagemAproveitamento;
    private List<EstatisticasEstudanteProvaResponseDTO> estatisticasPorProva;
}
