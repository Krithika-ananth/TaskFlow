package com.dayflow.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public final class PasswordResetMailer {
    private PasswordResetMailer() {}

    public static void send(String recipient, String link) throws Exception {
        String host = required("DAYFLOW_SMTP_HOST");
        String from = required("DAYFLOW_SMTP_FROM");
        String username = System.getenv("DAYFLOW_SMTP_USER");
        String password = System.getenv("DAYFLOW_SMTP_PASSWORD");
        String port = env("DAYFLOW_SMTP_PORT", "587");
        boolean auth = username != null && !username.isBlank();
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", Boolean.toString(auth));
        props.put("mail.smtp.starttls.enable", env("DAYFLOW_SMTP_STARTTLS", "true"));
        Session session = Session.getInstance(props, auth ? new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password == null ? "" : password);
            }
        } : null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject("Reset your Task Flow password", "UTF-8");
        message.setText("We received a request to reset your Task Flow password.\n\n"
                + "Use this link within 30 minutes to choose a new password:\n" + link
                + "\n\nIf you did not request this, you can ignore this email.", "UTF-8");
        Transport.send(message);
    }

    private static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException(key + " is not configured");
        return value;
    }
    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
