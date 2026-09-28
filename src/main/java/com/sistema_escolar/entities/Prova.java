package com.sistema_escolar.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
