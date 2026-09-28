package com.sistema_escolar.unit.service;

import com.sistema_escolar.services.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("enviarEmail deve enviar o e-mail quando o envio de e-mails estiver habilitado")
    void enviarEmail_EnviaEmail_QuandoEnvioDeEmailsHabilitado() {
        ReflectionTestUtils.setField(mailService, "habilitado", true);
        ReflectionTestUtils.setField(mailService, "emailFrom", "no-reply@example.com");
        ArgumentCaptor<SimpleMailMessage> mensagemCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        mailService.enviarEmail("fulano@example.com", "Assunto", "Mensagem");

        verify(javaMailSender).send(mensagemCaptor.capture());
        assertThat(mensagemCaptor.getValue().getFrom()).isEqualTo("no-reply@example.com");
        assertThat(mensagemCaptor.getValue().getTo()).containsExactly("fulano@example.com");
        assertThat(mensagemCaptor.getValue().getSubject()).isEqualTo("Assunto");
        assertThat(mensagemCaptor.getValue().getText()).isEqualTo("Mensagem");
    }

    @Test
    @DisplayName("enviarEmail não deve enviar nada quando o envio de e-mails estiver desabilitado")
    void enviarEmail_NaoEnviaEmail_QuandoEnvioDeEmailsDesabilitado() {
        ReflectionTestUtils.setField(mailService, "habilitado", false);

        mailService.enviarEmail("fulano@example.com", "Assunto", "Mensagem");

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("enviarEmail deve lançar RuntimeException quando o servidor de e-mail falhar")
    void enviarEmail_LancaRuntimeException_QuandoServidorDeEmailFalhar() {
        ReflectionTestUtils.setField(mailService, "habilitado", true);
        doThrow(new MailSendException("falha")).when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> mailService.enviarEmail("fulano@example.com", "Assunto", "Mensagem"))
                .withCauseInstanceOf(MailSendException.class);
    }
}
