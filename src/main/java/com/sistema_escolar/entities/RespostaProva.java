package com.sistema_escolar.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_resposta_prova")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RespostaProva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String resposta;

    @ManyToOne
    @JoinColumn(name = "estudante_id")
    @NotNull
    private Estudante estudante;

    @ManyToOne
    @JoinColumn(name = "questao_id")
    @NotNull
    private Questao questao;

    @ManyToOne
    @JoinColumn(name = "prova_id")
    @NotNull
    private Prova prova;

    private BigDecimal nota;

    private Boolean avaliada;

    private Boolean respondida;

}
