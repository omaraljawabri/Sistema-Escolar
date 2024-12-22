package com.sistema_escolar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Estudante extends Usuario{

    @ManyToOne
    @JoinColumn(name = "estudante_turma_id")
    private Turma turma;

    @OneToMany(mappedBy = "estudante")
    private List<Nota> notas;

    @ManyToMany(mappedBy = "estudantes")
    private List<Disciplina> disciplinas;
}
