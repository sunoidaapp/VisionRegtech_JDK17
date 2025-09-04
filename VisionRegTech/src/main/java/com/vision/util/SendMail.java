package com.vision.util;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendMail {

    public static void main(String[]args) throws IOException {

        final String username = "satyaprakash.b@sunoida.com";
        final String password = "satyasupraja";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        //props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props,
          new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        try {
		System.out.println("Start............");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            String ToAddress = "satyaprakash.b@sunoida.com"; //"ileliem@kdic.go.ke";
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(ToAddress));
            message.setSubject("Test From KDIC");
            message.setText("Hi Mr. Ashok Ji. How are you.");

            Transport.send(message);

            System.out.println("Mail Send successfylly !!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}