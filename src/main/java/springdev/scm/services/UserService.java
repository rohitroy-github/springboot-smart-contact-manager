package springdev.scm.services;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(String userId);

    User updateUser(User user);

    void deleteUser(String userId);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUsers();
}
