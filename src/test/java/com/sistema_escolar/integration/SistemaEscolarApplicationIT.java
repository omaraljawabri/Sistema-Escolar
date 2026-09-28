package com.sistema_escolar.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class SistemaEscolarApplicationIT extends AbstractIntegrationTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void healthChecks_RetornamUp_QuandoAplicacaoEBancoEstaoDisponiveis() {
		for (String probe : new String[]{"/actuator/health/liveness", "/actuator/health/readiness"}) {
			ResponseEntity<String> response = testRestTemplate.getForEntity(probe, String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("\"status\":\"UP\"");
		}
	}
}
