package mate.academy.car.sharing.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import mate.academy.car.sharing.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@RestController
@RequestMapping("/test-email")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String testEmail(@RequestParam String to) {
        try {
            System.out.println("Sending email to: " + to);
            emailService.sendEmail(to, "Test Email", "This is a test email from Car Rental App.");
            return "Email sent successfully to " + to;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }
}
