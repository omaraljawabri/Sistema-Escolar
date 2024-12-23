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
public class Professor extends Usuario{

    @OneToMany(mappedBy = "professor")
    private List<Turma> turmas;

    @ManyToOne
    @JoinColumn(name = "professor_disciplina_id")
    private Disciplina disciplina;

    public Professor(String email, String password, UserRole userRole, String verificationCode, LocalDateTime expirationCodeTime, boolean isVerified, String firstName, String lastName){
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(userRole);
        this.setVerificationCode(verificationCode);
        this.setExpirationCodeTime(expirationCodeTime);
        this.setIsVerified(isVerified);
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }
}
