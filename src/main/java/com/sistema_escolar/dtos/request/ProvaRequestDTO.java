package com.sistema_escolar.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProvaRequestDTO {

    @NotNull
    private List<QuestaoRequestDTO> questoes;

    @NotNull
    private BigDecimal valorTotal;

}
