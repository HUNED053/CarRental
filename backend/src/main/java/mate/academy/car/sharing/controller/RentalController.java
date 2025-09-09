package mate.academy.car.sharing.controller;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.dto.request.RentalRequestDto;
import mate.academy.car.sharing.dto.response.RentalResponseDto;
import mate.academy.car.sharing.entity.Rental;
import mate.academy.car.sharing.mapper.RentalMapper;
import mate.academy.car.sharing.service.RentalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import javax.mail.MessagingException;
import javax.validation.Valid;
import mate.academy.car.sharing.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final RentalMapper mapper;
    private final EmailService emailService;
    @Value("${admin.email}")
    private String adminEmail;


    @Operation(summary = "Add a new rental", description =
            "Add a new rental and decrease decrease car inventory by 1")
    @PostMapping
public RentalResponseDto add(@RequestBody @Valid RentalRequestDto requestDto) {
    Rental rental = mapper.mapToEntity(requestDto);
    Rental savedRental = rentalService.add(rental);
    RentalResponseDto responseDto = mapper.mapToDto(savedRental);

    try {
        emailService.sendEmail(
            responseDto.getUser().getEmail(),
            "Car Rental Confirmed",
            "Hello " + responseDto.getUser().getFirstName() + ",\n\n" +
            "Your rental for the car \"" + responseDto.getCar().getModel() + "\" has been successfully booked.\n" +
            "Start Date: " + responseDto.getRentalDate() + "\n" +
            "Return Date: " + responseDto.getReturnDate() + "\n\n" +
            "Thank you for choosing us!"
        );
    } catch (MessagingException e) {
        e.printStackTrace(); // Or log it
    }

    return responseDto;
}


    @Operation(summary = "get rentals by user ID and whether the rental is still active or not",
            description = "get rentals by user ID and whether the rental is still active or not")
    @GetMapping
    public List<RentalResponseDto> getByUserAndStatus(@RequestParam Long userId,
                                                      @RequestParam Boolean isActive) {
        return rentalService.getByUserAndStatus(userId, isActive).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "get specific rental", description = "get specific rental")
    @GetMapping("/{id}")
    public RentalResponseDto get(@PathVariable Long id) {
        return mapper.mapToDto(rentalService.getById(id));
    }

    @Operation(summary = "set actual return date", description =
            "set actual return date and increase car inventory by 1")
            @PostMapping("/{id}/return")
public RentalResponseDto returnRental(@PathVariable Long id) {
    Rental returnedRental = rentalService.returnRental(id);
    RentalResponseDto responseDto = mapper.mapToDto(returnedRental);

    try {
        // Notify user
        emailService.sendEmail(
            responseDto.getUser().getEmail(),
            "Car Returned",
            "Hello " + responseDto.getUser().getFirstName() + ",\n\n" +
            "You have successfully returned the car \"" + responseDto.getCar().getModel() + "\".\n" +
            "We hope you had a great experience. Looking forward to serving you again!"
        );

        // Notify admin
        emailService.sendEmail(
            adminEmail, // or ADMIN_EMAIL if hardcoded
            "Car Returned by User",
            "User " + responseDto.getUser().getFirstName() + " (email: " +
            responseDto.getUser().getEmail() + ") has returned the car \"" +
            responseDto.getCar().getModel() + "\".\nReturn Date: " +
            responseDto.getActualReturnDate()
        );

    } catch (MessagingException e) {
        e.printStackTrace(); // or use a logger
    }

    return responseDto;
}

            
}
