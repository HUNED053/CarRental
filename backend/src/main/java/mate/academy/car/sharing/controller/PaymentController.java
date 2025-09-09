package mate.academy.car.sharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.dto.response.DiscountEstimateDto;
import mate.academy.car.sharing.dto.response.PaymentResponseDto;
import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.Rental;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.mapper.PaymentMapper;
import mate.academy.car.sharing.service.EmailService;
import mate.academy.car.sharing.service.PaymentService;
import mate.academy.car.sharing.service.RentalService;
import mate.academy.car.sharing.service.TelegramNotificationService;
import mate.academy.car.sharing.service.UserService;
import mate.academy.car.sharing.service.stripe.StripePaymentService;
import mate.academy.car.sharing.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final RentalService rentalService;
    private final UserService userService;
    private final StripePaymentService stripePaymentService;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService telegramNotificationService;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;
     @Value("${admin.email}")
     private String adminEmail;
    @Operation(summary = "List payments for user with discount details")
    @GetMapping
    public List<PaymentResponseDto> getByUserId(@RequestParam Long userId) {
        User user = userService.getById(userId);
        List<Payment> payments = paymentService.getByUser(user);

        return payments.stream().map(payment -> {
            Rental r = rentalService.getById(payment.getRental().getId());
            long days = ChronoUnit.DAYS.between(r.getRentalDate(), r.getReturnDate());
            days = days < 1 ? 1 : days;
            BigDecimal original = r.getCar().getDailyFee()
                                   .multiply(BigDecimal.valueOf(days));

            BigDecimal discounted = payment.getAmountToPay();
            BigDecimal discountAmt = original.subtract(discounted);
            BigDecimal discountPct = discountAmt
                .divide(original, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

            PaymentResponseDto dto = paymentMapper.toDto(payment);
            dto.setOriginalAmount(original);
            dto.setDiscountAmount(discountAmt);
            dto.setDiscountPercent(discountPct);
            return dto;
        }).collect(Collectors.toList());
    }

    @Operation(summary = "Estimate fee + discount for a given rental")
    @GetMapping("/estimate")
    public DiscountEstimateDto estimate(
            @RequestParam Long userId,
            @RequestParam Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rentalDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        User user = userService.getById(userId);
        Rental r = new Rental();
        r.setUser(user);
        r.setCar(rentalService.getById(carId).getCar());
        r.setRentalDate(rentalDate.atStartOfDay());
        r.setReturnDate(returnDate.atStartOfDay());

        long days = ChronoUnit.DAYS.between(rentalDate, returnDate);
        days = days < 1 ? 1 : days;
        BigDecimal original = r.getCar().getDailyFee()
                               .multiply(BigDecimal.valueOf(days));

        BigDecimal total = stripePaymentService.calculateAmountToPay(r);
        BigDecimal discountAmt = original.subtract(total);
        BigDecimal discountPct = discountAmt
            .divide(original, 2, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        return new DiscountEstimateDto(original, discountPct, discountAmt, total);
    }

    @Operation(summary = "Create payment session")
    @PostMapping
    public PaymentResponseDto createPaymentSession(
            @Valid @RequestBody mate.academy.car.sharing.dto.request.PaymentRequestDto requestDto) {
        Payment payment = paymentMapper.toEntity(requestDto);
        Payment created = stripePaymentService.createPaymentSession(payment);
        return paymentMapper.toDto(created);
    }

    @Operation(summary = "Payment success callback")
@GetMapping("/success")
public ResponseEntity<String> handleSuccessPayment(
        @RequestParam("session_id") String sessionId) {
    Payment payment = stripePaymentService.handleSuccessPayment(sessionId);
    User user = payment.getRental().getUser();

    // Send email to customer
    try {
        emailService.sendEmail(
            user.getEmail(),
            "Payment Successful",
            "Hello " + user.getFirstName() + ",\n\n" +
            "Your payment of $" + payment.getAmountToPay() +
            " for the car \"" + payment.getRental().getCar().getModel() + "\" has been successfully processed.\n\n" +
            "Rental Period: " + payment.getRental().getRentalDate().toLocalDate() +
            " to " + payment.getRental().getReturnDate().toLocalDate() + "\n\n" +
            "Thank you for using our service!"
        );
    } catch (MessagingException e) {
        e.printStackTrace(); // Log this appropriately
    }

    // Send email to admin
    try {
        emailService.sendEmail(
            adminEmail,  // replace with actual admin email or load from config
            "New Payment Received",
            "Payment received:\n\n" +
            "Customer: " + user.getFirstName() + " " + user.getLastName() + "\n" +
            "Email: " + user.getEmail() + "\n" +
            "Car: " + payment.getRental().getCar().getModel() + "\n" +
            "Amount Paid: $" + payment.getAmountToPay() + "\n" +
            "Session ID: " + sessionId
        );
    } catch (MessagingException e) {
        e.printStackTrace();
    }

    return ResponseEntity.ok("Payment successful. Thank you!");
}


@Operation(summary = "Payment cancellation callback")
@GetMapping("/cancel")
public ResponseEntity<String> handleCancelPayment(@RequestParam("session_id") String sessionId) {
    Payment payment = stripePaymentService.getPaymentBySessionId(sessionId);
    User user = payment.getRental().getUser();

    // Notify customer
    try {
        emailService.sendEmail(
            user.getEmail(),
            "Payment Cancelled",
            "Hello " + user.getFirstName() + ",\n\n" +
            "Your payment for the car \"" + payment.getRental().getCar().getModel() +
            "\" has been cancelled.\n\n" +
            "If you have any questions or would like to try again, please contact us.\n\n" +
            "Thank you!"
        );
    } catch (MessagingException e) {
        e.printStackTrace();
    }

    // Notify admin
    try {
        emailService.sendEmail(
            adminEmail ,  // replace this with your real admin email
            "Payment Cancelled",
            "A payment has been cancelled:\n\n" +
            "Customer: " + user.getFirstName() + " " + user.getLastName() + "\n" +
            "Email: " + user.getEmail() + "\n" +
            "Car: " + payment.getRental().getCar().getModel() + "\n" +
            "Session ID: " + sessionId
        );
    } catch (MessagingException e) {
        e.printStackTrace();
    }

    return ResponseEntity.ok("Payment canceled. Please try again later.");
}
@GetMapping("/")
    public List<Payment> getAllPayments() {
        System.out.println("Fetching all payments...");
        return paymentService.getAll();
    }
    
    @PatchMapping("/{sessionId}/toggle-status")
    public Payment togglePaymentStatus(@PathVariable String sessionId) {
        return paymentService.toggleStatus(sessionId);
    }

}
