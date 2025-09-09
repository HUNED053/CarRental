package mate.academy.car.sharing.service;

import java.util.List;
import java.util.Optional;
import mate.academy.car.sharing.dto.request.UserRequestDto;
import mate.academy.car.sharing.entity.User;

public interface UserService extends AbstractService<User> {
    Optional<User> findByEmail(String email);

    User updateUserRole(Long id, String role);

    User updateProfileInfo(String email, UserRequestDto dto);

    User registerUser(UserRequestDto dto);
    List<User> getAllUsers();
    User toggleUserRole(Long id, String actingManagerEmail);
    void deleteUserById(Long id);
    void updatePassword(Long id, String rawPassword);
    User updateUserInfoById(Long id, UserRequestDto dto);

}
