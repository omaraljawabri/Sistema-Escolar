package com.sistema_escolar.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Professor extends Usuario{

    @OneToOne
    @JoinColumn(name = "professor_turma_id")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "professor_disciplina_id")
    private Disciplina disciplina;

}
