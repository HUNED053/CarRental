package mate.academy.car.sharing.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPaymentTest() {
        Payment payment = new Payment();
        payment.setId(1L);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment addedPayment = paymentService.add(payment);
        assertNotNull(addedPayment);
        assertEquals(payment, addedPayment);
    }

    @Test
    void getByIdExistingPaymentTest() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment retrieved = paymentService.getById(paymentId);
        assertNotNull(retrieved);
        assertEquals(payment, retrieved);
    }

    @Test
    void getByIdNonExistingPaymentTest() {
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> paymentService.getById(paymentId));
    }

    @Test
    void getAllTest() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();
        List<Payment> all = List.of(p1, p2);
        when(paymentRepository.findAll()).thenReturn(all);

        List<Payment> retrieved = paymentService.getAll();
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        assertEquals(p1, retrieved.get(0));
        assertEquals(p2, retrieved.get(1));
    }

    @Test
    void deleteTest() {
        Long paymentId = 1L;
        paymentService.delete(paymentId);
        verify(paymentRepository).deleteById(paymentId);
    }

    @Test
    void updatePaymentTest() {
        Payment payment = new Payment();
        payment.setId(1L);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment updated = paymentService.update(payment);
        assertNotNull(updated);
        assertEquals(payment, updated);
    }

    @Test
    void getByUserExistingPaymentTest() {
        User user = new User();
        Payment payment = new Payment();
        payment.setId(1L);
        // stub the new repository method
        when(paymentRepository.findAllByUser(user)).thenReturn(List.of(payment));

        List<Payment> retrieved = paymentService.getByUser(user);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(payment, retrieved.get(0));
    }

    @Test
    void getByUserNonExistingPaymentTest() {
        User user = new User();
        when(paymentRepository.findAllByUser(user)).thenReturn(List.of());  // empty list

        assertThrows(NoSuchElementException.class, () -> paymentService.getByUser(user));
    }

    @Test
    void findBySessionIdExistingPaymentTest() {
        String sessionId = "testSessionId";
        Payment payment = new Payment();
        payment.setId(1L);
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        Payment retrieved = paymentService.findBySessionId(sessionId);
        assertNotNull(retrieved);
        assertEquals(payment, retrieved);
    }

    @Test
    void findBySessionIdNonExistingPaymentTest() {
        String sessionId = "testSessionId";
        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> paymentService.findBySessionId(sessionId));
    }
}
