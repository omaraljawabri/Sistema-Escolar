package com.sistema_escolar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_disciplina")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany
    @JoinTable(name = "estudante_disciplina", joinColumns = @JoinColumn(name = "disciplina_id"),
            inverseJoinColumns = @JoinColumn(name = "estudante_id"))
    private List<Estudante> estudantes;

    @OneToMany(mappedBy = "disciplina")
    private List<Professor> professores;

    @OneToMany(mappedBy = "disciplina")
    private List<Prova> provas;
}