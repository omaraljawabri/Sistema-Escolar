package com.sistema_escolar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EstatisticasProvaResponseDTO {
    private Long provaId;
    private BigDecimal mediaTurma;
    private BigDecimal porcentagemAcimaDeSeis;
}
