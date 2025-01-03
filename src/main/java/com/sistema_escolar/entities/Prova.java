package com.sistema_escolar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @ManyToMany
    @JoinTable(name = "prova_questao", joinColumns = @JoinColumn(name = "prova_id"),
    inverseJoinColumns = @JoinColumn(name = "questao_id"))
    private List<Questao> questoes;

    @NotNull
    private BigDecimal valorTotal;

    private Boolean publicado;

    private LocalDateTime tempoDeExpiracao;

    private String emailProfessor;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @OneToMany(mappedBy = "prova")
    private List<RespostaProva> respostasProva;

    @OneToMany(mappedBy = "prova")
    private List<Nota> notas;
}
