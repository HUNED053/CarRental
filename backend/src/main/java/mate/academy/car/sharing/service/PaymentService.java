package mate.academy.car.sharing.service;

import mate.academy.car.sharing.entity.Payment;
import mate.academy.car.sharing.entity.User;
import java.util.List;

public interface PaymentService extends AbstractService<Payment> {
    /** Returns all payments made by a given user. */
    List<Payment> getByUser(User user);

    /** Finds a payment by its Stripe session ID. */
    Payment findBySessionId(String sessionId);
    Payment toggleStatus(String sessionId);
}
