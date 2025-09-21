package com.etl.policy.notification.email;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlBody);
    void sendEmail(String[] to, String subject, String htmlBody);
    void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String htmlBody);

    void send(String subject, String html);
}