package mate.academy.car.sharing.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;
    private String brand;

    @Enumerated(value = EnumType.STRING)
    private CarType type;

    private int inventory;
    private BigDecimal dailyFee;

    @Column(name = "car_image")
    private String carImage;

    public enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
    public Car() {
    }
    public Car(String model, String brand, CarType type, BigDecimal dailyFee, int inventory, String carImage) {
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.dailyFee = dailyFee;
        this.inventory = inventory;
        this.carImage = carImage;
    }
    
    public String getCarImage() {
        return carImage;
    }
    public void setCarImage(String imageUrl) {
        this.carImage = imageUrl;
    }
    
}
