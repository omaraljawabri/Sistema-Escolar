package com.sistema_escolar.entities;

import com.sistema_escolar.enums.UserRole;
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
public class Estudante extends Usuario{

    @ManyToOne
    @JoinColumn(name = "estudante_turma_id")
    private Turma turma;

    @OneToMany(mappedBy = "estudante")
    private List<Nota> notas;

    @ManyToMany(mappedBy = "estudantes")
    private List<Disciplina> disciplinas;

    public Estudante(String email, String password, UserRole userRole, String verificationCode, LocalDateTime expirationCodeTime, boolean isVerified){
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(userRole);
        this.setVerificationCode(verificationCode);
        this.setExpirationCodeTime(expirationCodeTime);
        this.setIsVerified(isVerified);
    }
}
