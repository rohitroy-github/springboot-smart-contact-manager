package springdev.scm.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import springdev.scm.services.UserService;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User saveUser(User user) {
        // Implementation to be added
        return null;
    }

    @Override
    public Optional<User> getUserById(String userId) {
        // Implementation to be added
        return Optional.empty();
    }

    @Override
    public User updateUser(User user) {
        // Implementation to be added
        return null;
    }

    @Override
    public void deleteUser(String userId) {
        // Implementation to be added
    }

    @Override
    public boolean isUserExist(String userId) {
        // Implementation to be added
        return false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        // Implementation to be added
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        // Implementation to be added
        return null;
    }
}
