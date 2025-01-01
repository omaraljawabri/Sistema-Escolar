package com.sistema_escolar.infra.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@SecurityScheme(
        name = "securityConfig",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String[] AUTH_WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/registrar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/verificar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/mudar-senha/requisicao").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/mudar-senha/verificar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/disciplina").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/professor", "/api/v1/turma/estudante").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/gerar-codigo/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/turma/gerar-codigo/professor").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/entrar").hasAnyRole("PROFESSOR", "ESTUDANTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/prova").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/prova").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/prova/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/prova/avaliada/**").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/questao").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/resposta-prova").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/resposta-prova").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/nota/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/estatisticas/turma/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/estatisticas/estudante").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/estatisticas/geral").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
