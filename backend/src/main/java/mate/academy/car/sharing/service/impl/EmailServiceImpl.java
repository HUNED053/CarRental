package mate.academy.car.sharing.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import mate.academy.car.sharing.service.EmailService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;  // Injecting Spring's JavaMailSender

    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        // Create a new MimeMessage instance
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        
        helper.setTo(to);              // Set the recipient email
        helper.setSubject(subject);    // Set the email subject
        helper.setText(text);          // Set the email body content

        try {
            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully to " + to);
        } catch (MailException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();  // This will show the full cause
            throw new MessagingException("Error while sending email", e);
        }
        
    }
}
