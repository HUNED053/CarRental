package mate.academy.car.sharing.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.repository.PaymentRepository;
import mate.academy.car.sharing.service.PaymentService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public Payment add(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Can't find payment by id: " + id));
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment update(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getByUser(User user) {
        List<Payment> list = paymentRepository.findAllByUser(user);
        if (list.isEmpty()) {
            throw new NoSuchElementException("No payments found for user: " + user);
        }
        return list;
    }

    @Override
    public Payment findBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new NoSuchElementException("Can't find payment by sessionId: " + sessionId));
    }

    public Payment toggleStatus(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        Payment.PaymentStatus oldStatus = payment.getStatus();
        Payment.PaymentStatus newStatus = oldStatus == Payment.PaymentStatus.PAID
                ? Payment.PaymentStatus.PENDING
                : Payment.PaymentStatus.PAID;

        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);

        System.out.printf("🔁 Payment ID %d status changed from %s to %s%n",
                payment.getId(), oldStatus, newStatus);

        return updatedPayment;
    }
    
}