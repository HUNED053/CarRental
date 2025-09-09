package mate.academy.car.sharing.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.Rental;
import mate.academy.car.sharing.exception.FailedSessionCreatingException;
import mate.academy.car.sharing.service.PaymentService;
import mate.academy.car.sharing.service.RentalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData.builder;

@RequiredArgsConstructor
@Service
public class StripePaymentService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal(3);

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${base.url}")
    private String baseUrl;

    private final RentalService rentalService;
    private final PaymentService paymentService;

    public Payment createPaymentSession(Payment payment) {
        Rental rental = rentalService.getById(payment.getRental().getId());
        BigDecimal amount = calculateAmountToPay(rental);
        payment.setAmountToPay(amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setRental(rental);

        SessionCreateParams.LineItem lineItem =
            SessionCreateParams.LineItem.builder()
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd")
                    .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                    .setProductData(builder()
                        .setName("Car rental – discounted")
                        .setDescription("Rental fee after loyalty discount")
                        .build())
                    .build())
                .setQuantity(1L)
                .build();

        SessionCreateParams params =
            SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(buildSuccessUrl())
                .setCancelUrl(buildFailureUrl())
                .addLineItem(lineItem)
                .build();

        try {
            Stripe.apiKey = secretKey;
            Session session = Session.create(params);

            if (payment.getType() == Payment.PaymentType.FINE) {
                Payment fine = new Payment();
                fine.setStatus(Payment.PaymentStatus.PENDING);
                fine.setType(Payment.PaymentType.FINE);
                fine.setRental(rental);
                fine.setSessionId(session.getId());
                fine.setSessionUrl(session.getUrl());
                fine.setAmountToPay(calculateFineAmountToPay(rental));
                return paymentService.add(fine);
            }

            payment.setSessionId(session.getId());
            payment.setSessionUrl(session.getUrl());
            return paymentService.add(payment);
        } catch (StripeException e) {
            throw new FailedSessionCreatingException("Can't create session", e);
        }
    }

    private BigDecimal calculateDiscount(Rental rental) {
        List<Payment> history;
        try {
            history = paymentService.getByUser(rental.getUser());
        } catch (NoSuchElementException e) {
            history = Collections.emptyList();
        }
        long paidCount = history.stream()
            .filter(p -> p.getStatus() == Payment.PaymentStatus.PAID)
            .count();
        long discountPercent = Math.min(paidCount, 50);
        return BigDecimal.valueOf(100 - discountPercent)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the discounted rental fee (dailyFee × days × loyalty discount).
     * Made public so it can be reused by the /payments/estimate endpoint.
     */
    public BigDecimal calculateAmountToPay(Rental rental) {
        long days = Duration.between(rental.getRentalDate(), rental.getReturnDate()).toDays();
        days = days < 1 ? 1 : days;
        BigDecimal base = rental.getCar().getDailyFee()
            .multiply(BigDecimal.valueOf(days));
        BigDecimal discount = calculateDiscount(rental);
        return base.multiply(discount);
    }

    private BigDecimal calculateFineAmountToPay(Rental rental) {
        long overdue = Duration.between(rental.getReturnDate(), LocalDateTime.now()).toDays();
        overdue = overdue < 1 ? 1 : overdue;
        BigDecimal finePart = rental.getCar().getDailyFee()
            .multiply(BigDecimal.valueOf(overdue))
            .multiply(FINE_MULTIPLIER);
        return finePart.add(calculateAmountToPay(rental));
    }

    private String buildSuccessUrl() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/success")
            .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
            .build()
            .toUriString();
    }

    private String buildFailureUrl() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/cancel")
            .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
            .build()
            .toUriString();
    }

    public Payment handleSuccessPayment(String sessionId) {
        Payment payment = paymentService.findBySessionId(sessionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        return paymentService.add(payment);
    }
    public Payment getPaymentBySessionId(String sessionId) {
        return paymentService.findBySessionId(sessionId);
    }
    
}
