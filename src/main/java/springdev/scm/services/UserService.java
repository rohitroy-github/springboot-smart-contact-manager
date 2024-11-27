package springdev.scm.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;


public interface UserService {

    User saveUser(User user);

    User registerUser(UserForm userForm);

    User saveOAuthenticatedUser(DefaultOAuth2User oAuthenticatedUser, String oAuthClient);

    Optional<User> getUserById(String userId);

    Optional<User> updateUser(User user);

    void deleteUser(String userId);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUsers();
}
