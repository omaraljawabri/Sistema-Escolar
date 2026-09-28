package com.sistema_escolar.integration;

import com.sistema_escolar.dtos.response.LoginResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static com.sistema_escolar.utils.EntityUtils.criarLoginRequestDTO;
import static com.sistema_escolar.utils.EntityUtils.criarRegisterRequestDTO;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "api.sistema-escolar.mail.enabled=false")
class EmailDesabilitadoIT extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("registrar deve criar a conta já verificada, sem enviar e-mail, permitindo login imediato quando o envio de e-mails estiver desabilitado")
    void registrar_CriaContaVerificadaSemEnviarEmail_QuandoEnvioDeEmailsDesabilitado() {
        ResponseEntity<Void> registro
                = testRestTemplate.postForEntity("/api/v1/auth/registrar", criarRegisterRequestDTO(), Void.class);
        assertThat(registro.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(emailsRecebidosPor("fulano@example.com")).isZero();

        ResponseEntity<LoginResponseDTO> login
                = testRestTemplate.postForEntity("/api/v1/auth/login", criarLoginRequestDTO(), LoginResponseDTO.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        assertThat(login.getBody().getToken()).isNotBlank();
    }
}
