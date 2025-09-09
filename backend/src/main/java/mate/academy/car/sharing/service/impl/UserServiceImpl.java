package mate.academy.car.sharing.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.car.sharing.dto.request.UserRequestDto;
import mate.academy.car.sharing.entity.User;
import mate.academy.car.sharing.exception.EmailAlreadyExistsException;
import mate.academy.car.sharing.mapper.UserMapper;
import mate.academy.car.sharing.repository.UserRepository;
import mate.academy.car.sharing.service.UserService;
import mate.academy.car.sharing.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    public User add(User user) {
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Can't find user by id: " + id));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateUserRole(Long id, String role) {
        User userFromDb = userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Not found user with id: " + id)
        );
        userFromDb.setRole(User.Role.valueOf(role));
        return userFromDb;
    }

    @Override
    @Transactional
    public User updateProfileInfo(String email, UserRequestDto dto) {
        User userFromDb = userRepository.findByEmail(email).orElseThrow(
                () -> new NoSuchElementException(
                        "Not found profile info for user with email: " + email));
        userFromDb.setEmail(dto.getEmail());
        userFromDb.setFirstName(dto.getFirstName());
        userFromDb.setLastName(dto.getLastName());
        userFromDb.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userFromDb;
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }
    
  @Override
public User registerUser(UserRequestDto dto) {
    User user = userMapper.mapToEntity(dto);

    // Check if the email already exists
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new EmailAlreadyExistsException("Email already exists");
    }

    // Encode the password
    user.setPassword(passwordEncoder.encode(dto.getPassword()));

    // Save the user
    User savedUser = userRepository.save(user);

    // Send email after successful registration
    try {
        emailService.sendEmail(
            user.getEmail(),
            "Welcome to Car Sharing!",
            "Hi " + user.getFirstName() + ",\n\nThank you for registering with us.\n\nHappy riding!"
        );
    } catch (MessagingException e) {
        System.err.println("Failed to send welcome email to: " + user.getEmail());
        e.printStackTrace();
    }

    return savedUser;
}

 @Autowired

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User toggleUserRole(Long id, String actingManagerEmail) {
        User user = userRepository.findById(id).orElseThrow();
    
        // Store the old role before changing
        User.Role oldRole = user.getRole();
    
        // Toggle role
        User.Role newRole = oldRole == User.Role.CUSTOMER
                ? User.Role.MANAGER
                : User.Role.CUSTOMER;
    
        user.setRole(newRole);
    
        User updatedUser = userRepository.save(user);
    
        System.out.printf("🔁 %s changed %s's role from %s to %s%n",
            actingManagerEmail, user.getEmail(), oldRole, updatedUser.getRole());
    
        return updatedUser;
    }
    @Override
public void deleteUserById(Long id) {
    if (!userRepository.existsById(id)) {
        throw new RuntimeException("User not found with id " + id);
    }
    userRepository.deleteById(id);
}
  @Override

public void updatePassword(Long id, String rawPassword) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setPassword(passwordEncoder.encode(rawPassword));
    userRepository.save(user);
}
@Override
@Transactional
public User updateUserInfoById(Long id, UserRequestDto dto) {
    User userFromDb = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

    userFromDb.setEmail(dto.getEmail());
    userFromDb.setFirstName(dto.getFirstName());
    userFromDb.setLastName(dto.getLastName());

    return userRepository.save(userFromDb);
}


}
