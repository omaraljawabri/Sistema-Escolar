package com.sistema_escolar.infra.security;

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
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/change-password/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/disciplina").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/professor", "/api/v1/turma/estudante").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/generate-code/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/turma/generate-code/professor").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/turma/join").hasAnyRole("PROFESSOR", "ESTUDANTE")
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
