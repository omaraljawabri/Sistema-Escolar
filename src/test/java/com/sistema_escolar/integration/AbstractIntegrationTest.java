package com.sistema_escolar.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.flyway.clean-disabled=false",
                "api.sistema-escolar.auth.token.secret=segredo-de-teste"
        })
@Import(AbstractIntegrationTest.FlywayCleanConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    static final GenericContainer<?> MAILPIT = new GenericContainer<>("axllent/mailpit:v1.31.3")
            .withExposedPorts(1025, 8025);

    static {
        POSTGRES.start();
        MAILPIT.start();
    }

    @DynamicPropertySource
    static void mailProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", MAILPIT::getHost);
        registry.add("spring.mail.port", () -> MAILPIT.getMappedPort(1025));
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

    @TestConfiguration(proxyBeanMethods = false)
    static class FlywayCleanConfig {
        @Bean
        FlywayMigrationStrategy cleanMigrateStrategy() {
            return flyway -> {
                flyway.clean();
                flyway.migrate();
            };
        }
    }
}
