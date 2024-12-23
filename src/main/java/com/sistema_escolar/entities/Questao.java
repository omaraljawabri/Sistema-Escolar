package com.sistema_escolar.entities;

import com.sistema_escolar.utils.TipoQuestao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_questao")
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoQuestao tipoQuestao;

    private String pergunta;

    private List<String> alternativas;

    private BigDecimal valor;

    @ManyToMany(mappedBy = "questoes")
    private List<Prova> provas;
}
