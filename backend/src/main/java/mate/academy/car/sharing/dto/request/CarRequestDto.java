package mate.academy.car.sharing.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class CarRequestDto {
    @NotBlank(message = "Model must not be empty")
    private String model;
    @NotBlank(message = "Brand must not be empty")
    private String brand;
    @NotBlank(message = "Type must not be empty")
    private String type;
    @Positive(message = "Inventory must be a positive value")
    private int inventory;
    @NotNull(message = "DailyFee must not be null")
    @Positive(message = "DailyFee must be a positive value")
    private BigDecimal dailyFee;
    private String carImage;
    public CarRequestDto(String brand, String model, String type, BigDecimal dailyFee , int inventory , String carImage) {
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.dailyFee = dailyFee;
        this.inventory = inventory;
        this.carImage = carImage;
    }
    public CarRequestDto() {
        // Default constructor needed for frameworks like Jackson or when manually setting fields
    }
    
}
