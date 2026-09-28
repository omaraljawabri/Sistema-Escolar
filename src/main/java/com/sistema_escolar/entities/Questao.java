package com.sistema_escolar.entities;

import com.sistema_escolar.utils.enums.TipoQuestao;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
