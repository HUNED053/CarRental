package mate.academy.car.sharing.dto.response;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.car.sharing.entity.Payment;

@Data
public class PaymentResponseDto {
    private String sessionId;
    private String sessionUrl;
    private BigDecimal amountToPay;          // discounted total
    private BigDecimal originalAmount;       // before discount
    private BigDecimal discountPercent;      // e.g. 1.00 for 1%
    private BigDecimal discountAmount;       // originalAmount - amountToPay
    private Long rentalId;
    private String type;
    private String status;
}