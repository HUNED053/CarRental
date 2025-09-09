/*package mate.academy.car.sharing.dto.request;

import java.time.LocalDateTime;
import lombok.Data;
import javax.validation.constraints.Future;
//import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
//import javax.validation.constraints.PastOrPresent;

@Data
public class RentalRequestDto {
    @NotNull(message = "Rental date must not be null")
   //@FutureOrPresent(message = "Rental date cannot be in the past")
private LocalDateTime rentalDate;

    @NotNull(message = "Return date must not be null")
    @Future(message = "Return date must be in the future")
    private LocalDateTime returnDate;
    @NotNull(message = "Car ID must not be null")
    private Long carId;
    @NotNull(message = "User ID must not be null")
    private Long userId;
}
*/
package mate.academy.car.sharing.dto.request;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalRequestDto {
    @NotNull(message = "Rental date must not be null")
    private LocalDateTime rentalDate;

    @NotNull(message = "Return date must not be null")
    @Future(message = "Return date must be in the future")
    private LocalDateTime returnDate;

    @NotNull(message = "Car ID must not be null")
    private Long carId;

    @NotNull(message = "User ID must not be null")
    private Long userId;
    
    // You may calculate and optionally accept this at controller/service level
    private Integer daysBooked;
}
