package com.sistema_escolar.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "api.sistema-escolar.auth.token.secret=segredo-de-teste")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    static final GenericContainer<?> MAILPIT = new GenericContainer<>("axllent/mailpit:v1.31.3")
            .withExposedPorts(1025, 8025);

    static {
        Startables.deepStart(POSTGRES, MAILPIT).join();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void mailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", MAILPIT::getHost);
        registry.add("spring.mail.port", () -> MAILPIT.getMappedPort(1025));
    }

    @BeforeEach
    void limparBancoDeDados() {
        List<String> tabelas = jdbcTemplate.queryForList("""
                select tablename from pg_tables
                where schemaname = 'public' and tablename <> 'flyway_schema_history'
                """, String.class);
        jdbcTemplate.execute("truncate table " + String.join(", ", tabelas) + " restart identity cascade");
    }

    @BeforeEach
    void limparCaixaDeEmails() {
        mailpit().delete().uri("/api/v1/messages").retrieve().toBodilessEntity();
    }

    protected long emailsRecebidosPor(String destinatario) {
        JsonNode resultado = mailpit().get()
                .uri("/api/v1/search?query={query}", "to:" + destinatario)
                .retrieve()
                .body(JsonNode.class);
        return resultado == null ? 0 : resultado.path("messages_count").asLong();
    }

    private static RestClient mailpit() {
        return RestClient.create("http://" + MAILPIT.getHost() + ":" + MAILPIT.getMappedPort(8025));
    }
}
