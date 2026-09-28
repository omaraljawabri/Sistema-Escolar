package com.sistema_escolar.entities;

import com.sistema_escolar.utils.enums.TipoQuestao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_questao")
@Builder
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoQuestao tipoQuestao;

    @NotNull
    private String pergunta;

    @ElementCollection
    @CollectionTable(name = "alternativas_questao", joinColumns = @JoinColumn(name = "questao_id"))
    @Column(name = "alternativas")
    private List<String> alternativas;

    @NotNull
    private BigDecimal valor;

    private String criadoPor;

    private String atualizadoPor;

    private String respostaCorreta;

    @ManyToMany(mappedBy = "questoes")
    private List<Prova> provas;

    @OneToMany(mappedBy = "questao")
    private List<RespostaProva> respostasProva;
}
