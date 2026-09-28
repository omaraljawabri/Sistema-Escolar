package com.sistema_escolar.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_redefinir_senha")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RedefinirSenha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String codigoDeVerificacao;

    @NotNull
    private LocalDateTime tempoDeExpiracaoCodigo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
