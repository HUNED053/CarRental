package mate.academy.car.sharing.controller;
import mate.academy.car.sharing.repository.UserRepository;
import mate.academy.car.sharing.repository.CarRepository;
import mate.academy.car.sharing.repository.PaymentRepository;
import mate.academy.car.sharing.repository.RentalRepository;
import java.util.List;
//import java.util.Collections;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.dto.request.UserRequestDto;
import mate.academy.car.sharing.dto.response.UserResponseDto;
import mate.academy.car.sharing.entity.Car;
import mate.academy.car.sharing.entity.Rental;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.exception.EmailAlreadyExistsException;
import mate.academy.car.sharing.mapper.UserMapper;
import mate.academy.car.sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import mate.academy.car.sharing.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    @Autowired
    private final EmailService emailService;


    @Operation(summary = "Get current user info", description = "Get current user info")
    @GetMapping("/me")
    public UserResponseDto get(Authentication auth) {
        String email = auth.getName();
        User user = userService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with email " + email + " not found"));
        return mapper.mapToDto(user);
    }

    @Operation(summary = "Update user role", description = "Update user role")
    @PutMapping("{id}/role")
    public UserResponseDto updateRole(@PathVariable Long id, @RequestBody String role) {
        return mapper.mapToDto(userService.updateUserRole(id, role));
    }

    @Operation(summary = "Update current user", description = "Update current user")
@PutMapping("/me")
public UserResponseDto updateUser(Authentication auth, @Valid @RequestBody UserRequestDto dto) {
    String email = auth.getName();
    UserResponseDto updatedUser = mapper.mapToDto(userService.updateProfileInfo(email, dto));

    try {
        emailService.sendEmail(
            email,
            "Profile Updated",
            "Hello, your profile information was successfully updated."
        );
    } catch (MessagingException e) {
        // Log the error but don’t fail the request
        System.err.println("Failed to send profile update email to: " + email);
        e.printStackTrace();
    }

    return updatedUser;
}


@PostMapping("/register")
@Operation(summary = "Register a new user", description = "Registers a new user with unique email")
public ResponseEntity<?> registerUser(@RequestBody UserRequestDto userDto) {
    try {
        User savedUser = userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    } catch (EmailAlreadyExistsException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("message", e.getMessage()));
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of("message", "Email already exists"));
    } catch (Exception e) {
        e.printStackTrace();  // Add this to see the full error
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("message", "Something went wrong"));
    }
}


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/toggle-role")
    @PreAuthorize("hasAuthority('MANAGER')")
    public User toggleUserRole(@PathVariable Long id, Authentication authentication) {
        String actingManagerEmail = authentication.getName(); // Who changed the role
        return userService.toggleUserRole(id, actingManagerEmail);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
    
            // Step 1: Get all rentals for the user
            List<Rental> rentals = rentalRepository.findByUserId(id);
    
            for (Rental rental : rentals) {
                Long rentalId = rental.getId();
    
                // Step 2: Delete payments for this rental
                paymentRepository.deleteByRentalId(rentalId);
    
                // Step 3: Delete the rental itself
                rentalRepository.deleteById(rentalId);
            }
    
            // Step 4: Get user email before deleting
            User user = userRepository.findById(id).orElse(null);
            String email = (user != null) ? user.getEmail() : null;
    
            // Step 5: Delete the user
            userRepository.deleteById(id);
    
            // Step 6: Send email (optional)
            if (email != null) {
                try {
                    emailService.sendEmail(
                        email,
                        "Account Deleted",
                        "Your account has been deleted by an admin. If you believe this was a mistake, please contact support."
                    );
                } catch (MessagingException e) {
                    System.err.println("Failed to send deletion email to: " + email);
                    e.printStackTrace();
                }
            }
    
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user and related data.");
        }
    }
    

@PutMapping("/{id}/reset-password")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
    String newPassword = body.get("newPassword");

    try {
        userService.updatePassword(id, newPassword); // just update

        User user = userService.getById(id); // implement this if not already
        emailService.sendEmail(
            user.getEmail(),
            "Password Reset by Admin",
            "Hello, your password was reset by the Admin. Your new password is: " + newPassword + ". If you did not request this, please contact support."
        );
        

        return ResponseEntity.ok("Password reset successful");
    } catch (MessagingException e) {
        System.err.println("Failed to send admin update notification to user.");
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password");
    }
}

@PutMapping("/{id}")
public ResponseEntity<UserResponseDto> updateUserByAdmin(
        @PathVariable Long id,
        @RequestBody UserRequestDto dto) {
    User updatedUser = userService.updateUserInfoById(id, dto);
    try {
        emailService.sendEmail(
            updatedUser.getEmail(),
            "Profile Updated by Admin",
            "Hello, your profile details were modified by an admin. If you did not request this, please contact support."
        );
    } catch (MessagingException e) {
        System.err.println("Failed to send admin update notification to: " + updatedUser.getEmail());
    }
    
    return ResponseEntity.ok(mapper.mapToDto(updatedUser));
}
}
