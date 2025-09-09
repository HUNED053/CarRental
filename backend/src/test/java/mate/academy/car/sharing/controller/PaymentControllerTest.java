package mate.academy.car.sharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import mate.academy.car.sharing.dto.request.PaymentRequestDto;
import mate.academy.car.sharing.dto.response.PaymentResponseDto;
import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.mapper.PaymentMapper;
import mate.academy.car.sharing.service.PaymentService;
import mate.academy.car.sharing.service.TelegramNotificationService;
import mate.academy.car.sharing.service.UserService;
import mate.academy.car.sharing.service.stripe.StripePaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    @Mock private PaymentMapper paymentMapper;
    @Mock private PaymentService paymentService;
    @Mock private StripePaymentService stripePaymentService;
    @Mock private UserService userService;
    @Mock private TelegramNotificationService telegramNotificationService;
    @InjectMocks private PaymentController paymentController;

    @Test
    public void testGetByUserId_ReturnsListOfPaymentResponseDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Payment payment = new Payment();

        when(userService.getById(userId)).thenReturn(user);
        // now returns a List<Payment>
        when(paymentService.getByUser(user)).thenReturn(List.of(payment));
        PaymentResponseDto dto = new PaymentResponseDto();
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        List<PaymentResponseDto> result = paymentController.getByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    public void testCreatePaymentSession_ReturnsPaymentResponseDto() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        Payment payment = new Payment();

        when(paymentMapper.toEntity(requestDto)).thenReturn(payment);
        when(stripePaymentService.createPaymentSession(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(new PaymentResponseDto());

        PaymentResponseDto result = paymentController.createPaymentSession(requestDto);

        assertNotNull(result);
    }

    @Test
    public void testHandleSuccessPayment_ReturnsResponseEntity() {
        String sessionId = "some_session_id";

        ResponseEntity<String> response = paymentController.handleSuccessPayment(sessionId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment successful. Thank you!", response.getBody());
    }
}
