package com.sistema_escolar.services;

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
    public void sendEmail(String email, String verificationCode, String subject, String textMessage){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emailFrom);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(textMessage);
            javaMailSender.send(simpleMailMessage);
        } catch (MailException exception){
            throw new RuntimeException(exception);
        }
    }
}
