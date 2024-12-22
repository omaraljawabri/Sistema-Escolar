package com.sistema_escolar.services;

import com.sistema_escolar.dtos.request.LoginRequestDTO;
import com.sistema_escolar.dtos.request.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendVerificationEmail(RegisterRequestDTO registerRequestDTO, String verificationCode){
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?code=" + verificationCode;
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emailFrom);
            simpleMailMessage.setTo(registerRequestDTO.getEmail());
            simpleMailMessage.setSubject("Validação de cadastro");
            simpleMailMessage.setText("Olá, recebemos uma solicitação de cadastro na nossa plataforma utilizando este e-mail. \nCaso deseje validar sua conta em nossa plataforma, clique no link abaixo: \n"+verificationLink);
            javaMailSender.send(simpleMailMessage);
        } catch (MailException exception){
            throw new RuntimeException(exception);
        }
    }

}
