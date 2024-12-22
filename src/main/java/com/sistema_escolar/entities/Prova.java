package com.sistema_escolar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tb_prova")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "prova_questao", joinColumns = @JoinColumn(name = "prova_id"),
            inverseJoinColumns = @JoinColumn(name = "questao_id"))
    private List<Questao> questoes;

    private BigDecimal valorTotal;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;
}
