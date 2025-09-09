package mate.academy.car.sharing.dto.response;

import java.time.LocalDateTime;
import lombok.Data;
//import mate.academy.car.sharing.dto.response.CarResponseDto;
//import mate.academy.car.sharing.dto.response.UserResponseDto;

@Data
public class RentalResponseDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private CarResponseDto car;
    private UserResponseDto user;
    private Long carId;
private Long userId;
}
