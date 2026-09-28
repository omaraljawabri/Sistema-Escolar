package com.sistema_escolar.entities;

import com.sistema_escolar.utils.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "primeiro nome deve ser preenchido")
    private String nome;

    @NotNull(message = "sobrenome deve ser preenchido")
    private String sobrenome;

    @NotNull(message = "email deve ser preenchido")
    private String email;

    @NotNull(message = "senha deve ser preenchida")
    private String senha;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "role deve ser preenchida")
    private UserRole role;

    private Boolean verificado;

    private LocalDateTime tempoDeExpiracaoCodigo;

    private String codigoDeVerificacao;

    @OneToMany(mappedBy = "usuario")
    private List<RedefinirSenha> redefinirSenha;

    @Override
    public String getUsername(){
        return this.email;
    }

    public String getPassword(){
        return this.senha;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (this.role == UserRole.PROFESSOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
