package com.sistema_escolar.entities;

import com.sistema_escolar.utils.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Professor extends Usuario{

    @OneToMany(mappedBy = "professor")
    private List<Turma> turmas;

    @ManyToOne
    @JoinColumn(name = "professor_disciplina_id")
    private Disciplina disciplina;

    public Professor(String email, String password, UserRole userRole, String verificationCode, LocalDateTime codeExpirationTime, boolean isVerified, String firstName, String lastName){
        this.setEmail(email);
        this.setSenha(password);
        this.setRole(userRole);
        this.setCodigoDeVerificacao(verificationCode);
        this.setTempoDeExpiracaoCodigo(codeExpirationTime);
        this.setVerificado(isVerified);
        this.setNome(firstName);
        this.setSobrenome(lastName);
    }
}
