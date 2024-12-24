package com.sistema_escolar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tb_prova")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "prova")
    private List<Questao> questoes;

    private BigDecimal valorTotal;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;
}
