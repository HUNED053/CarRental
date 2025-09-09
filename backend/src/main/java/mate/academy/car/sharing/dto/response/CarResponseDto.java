package mate.academy.car.sharing.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private String type;
    private int inventory;
    private BigDecimal dailyFee;
    private String carImage;

    
    public CarResponseDto(Long id, String brand, String model, String type,
                          BigDecimal dailyFee, int inventory, String carImage) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.dailyFee = dailyFee;
        this.inventory = inventory;
        this.carImage = carImage;
    }
    public CarResponseDto() {
        // Default constructor needed for frameworks like Jackson or when manually setting fields
    }
    
}
