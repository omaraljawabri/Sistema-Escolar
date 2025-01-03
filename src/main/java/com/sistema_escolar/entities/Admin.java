package com.sistema_escolar.entities;

import com.sistema_escolar.utils.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admin extends Usuario{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Admin(String email, String password, UserRole userRole, String verificationCode, LocalDateTime codeExpirationTime, boolean isVerified, String firstName, String lastName){
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
