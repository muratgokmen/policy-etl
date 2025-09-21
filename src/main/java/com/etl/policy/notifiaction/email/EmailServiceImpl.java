package com.etl.policy.notifiaction.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {


    @Value("${spring.mail.username:no-reply@example.com}")
    private String from;

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        sendEmail(new String[]{to}, null, null, subject, htmlBody);
    }

    @Override
    public void sendEmail(String[] to, String subject, String htmlBody) {
        sendEmail(to, null, null, subject, htmlBody);
    }

    @Override
    public void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String htmlBody) {
        try {
           /* MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            if (to != null && to.length > 0) helper.setTo(to);
            if (cc != null && cc.length > 0) helper.setCc(cc);
            if (bcc != null && bcc.length > 0) helper.setBcc(bcc);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);*/
        } catch (Exception e) {
            log.error("Email sending failed. subject={}", subject, e);
            throw new IllegalStateException("Email sending failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void send(String subject, String html) {

    }
}