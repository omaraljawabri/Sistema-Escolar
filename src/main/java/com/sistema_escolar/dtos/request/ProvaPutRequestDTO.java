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
public class ProvaPutRequestDTO {

    @NotNull
    private List<QuestaoPutRequestDTO> questoes;

    @NotNull
    private BigDecimal valorTotal;

}
