package com.example.chat_application.service;

import com.example.chat_application.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSendService {

    private final JavaMailSender mailSender;

    public EmailSendService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(SendEmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), false); // false = plain text

            if (request.getReplyTo() != null) {
                helper.setReplyTo(request.getReplyTo());
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
