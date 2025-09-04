//package com.vision.wb;
//
//import java.util.Properties;
//
//import jakarta.activation.DataHandler;
//import jakarta.activation.DataSource;
//import javax.activation.FileDataSource;
//import jakarta.mail.Authenticator;
//import jakarta.mail.BodyPart;
//import jakarta.mail.Message;
//import jakarta.mail.MessagingException;
//import jakarta.mail.PasswordAuthentication;
//import jakarta.mail.Session;
//import jakarta.mail.Transport;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeBodyPart;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.mail.internet.MimeMultipart;
//
//public class EmailSenderWithAttachment {
//    public static void main(String[] args) {
//        // Sender's email address
//        String senderEmail = "aravind.v0246@gmail.com";
//        // Sender's email password
//        String password = "ocxqeiqbojassbjw";
//        // Recipient's email address
//        String recipientEmail = "aravind.velmurugan@sunoida.com";
//        // Email subject
//        String subject = "Test Email with Attachment";
//        // Email message
//        String message = "This is a test email sent from Sunoida with an attachment.";
//
//        // SMTP server details
//        String host = "smtp.gmail.com";
//        String port = "587"; // You can also use other ports like 25, 465
//
//        // Set properties
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", host);
//        properties.put("mail.smtp.port", port);
//
//        // Get the default Session object
//        Session session = Session.getInstance(properties, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(senderEmail, password);
//            }
//        });
//
//        try {
//            // Create a default MimeMessage object
//            MimeMessage mimeMessage = new MimeMessage(session);
//            // Set From: header field
//            mimeMessage.setFrom(new InternetAddress(senderEmail));
//            // Set To: header field
//            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
//            // Set Subject: header field
//            mimeMessage.setSubject(subject);
//
//            // Create a multipart message
//            MimeMultipart multipart = new MimeMultipart();
//
//            // Create the text part
//            BodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setText(message);
//
//            // Add text part to multipart
//            multipart.addBodyPart(messageBodyPart);
//
//            // Attach fileC:\Rg_logs
//            String filePath = "C:\\Rg_logs\\TEST_ERR.txt";
//            messageBodyPart = new MimeBodyPart();
//            DataSource source = new FileDataSource(filePath);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName("Test_mail.txt");
//            multipart.addBodyPart(messageBodyPart);
//
//            // Set the multipart message to the email
//            mimeMessage.setContent(multipart);
//
//            // Send message
//            Transport.send(mimeMessage);
//            System.out.println("Email with attachment sent successfully!");
//        } catch (MessagingException e) {
//            System.err.println("Failed to send email with attachment. Error message: " + e.getMessage());
//        }
//    }
//}
//
