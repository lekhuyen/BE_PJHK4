//package fpt.aptech.server_be.service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//
//public class ContactEmailService {
//
//    private final JavaMailSender mailSender;
//
//    public ContactEmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendEmail(String to, String subject, String body) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(body, true);  // Set to true to send HTML content if needed
//
//        mailSender.send(mimeMessage);
//    }
//
//}
