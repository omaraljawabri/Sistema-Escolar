package com.sistema_escolar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "turma")
    private Professor professor;

    @OneToMany(mappedBy = "turma")
    private List<Estudante> estudantes;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;
}
