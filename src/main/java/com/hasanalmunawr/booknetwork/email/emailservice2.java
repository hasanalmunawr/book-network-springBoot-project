package com.hasanalmunawr.booknetwork.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class emailservice2 {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendEmail2(
            String username,
            String confirmationUrl,
            String recipientEmail
    ) {

//        try {
//            String code = generateActivationCode(6);
//            String htmlContent = getMessageEmail(username, code, confirmationUrl);
//            MimeMessage message = mailSender.createMimeMessage();
//
//            message.setFrom(senderEmail);
//            message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
//            message.setSubject("Account Activation"); // Set your email subject here
//            message.setContent(htmlContent, "text/html; charset=utf-8");
//            mailSender.send(message);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }

    }
}
