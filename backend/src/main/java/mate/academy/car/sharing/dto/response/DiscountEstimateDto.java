package mate.academy.car.sharing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DiscountEstimateDto {
    private BigDecimal originalAmount;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal totalAfterDiscount;
}
