package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;

import springdev.scm.repositories.UserRepo;
import springdev.scm.services.UserService;
import springdev.scm.entities.Providers;
import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;
import springdev.scm.helper.AppConstants;
import springdev.scm.helper.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        // :> generating random userId for newly created user
        String generatedUserId = UUID.randomUUID().toString();
        user.setUserId(generatedUserId);

        // :> encoding the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // :> configuring user roles
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        return userRepo.save(user);
    }

    @Override
    public User saveOAuthenticatedUser(DefaultOAuth2User oAuthenticatedUser, String oAuthClient) {

        User newUser = new User();

        String email = oAuthenticatedUser.getAttribute("email").toString();
        String name = oAuthenticatedUser.getAttribute("name").toString();

        // :> client == google
        if (oAuthClient.equalsIgnoreCase("google")) {

            String profilePicture = oAuthenticatedUser.getAttribute("picture").toString();

            newUser.setUserId(UUID.randomUUID().toString());
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setProfilePicture(profilePicture);
            // :> setting dummy password for all oauthenticated users
            newUser.setPassword(passwordEncoder.encode("oauth_default_password"));
            newUser.setAbout("I'm a new user authenticated using Google OAuth2.");
            newUser.setRoleList(List.of(AppConstants.ROLE_USER));
            newUser.setProvider(Providers.GOOGLE);
            newUser.setEnabled(true);
            newUser.setEmailVerified(true);
            newUser.setProviderUserId(oAuthenticatedUser.getName());
        } else if (oAuthClient.equalsIgnoreCase("github")) {

            String profilePicture = oAuthenticatedUser.getAttribute("avatar_url").toString();

            newUser.setUserId(UUID.randomUUID().toString());
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setProfilePicture(profilePicture);
            newUser.setPassword(passwordEncoder.encode("oauth_default_password"));
            newUser.setAbout("I'm a new user authenticated using Github OAuth2.");
            newUser.setRoleList(List.of(AppConstants.ROLE_USER));
            newUser.setProvider(Providers.GITHUB);
            newUser.setEnabled(true);
            newUser.setEmailVerified(true);
            newUser.setProviderUserId(oAuthenticatedUser.getName());

        }

        return userRepo.save(newUser);
    }

    @Override
    public User registerUser(UserForm userForm) {

        // :> creating a new User object
        User newUser = new User();

        // :> generating a user-id for the new user
        String generatedUserId = UUID.randomUUID().toString();

        newUser.setUserId(generatedUserId);
        newUser.setName(userForm.getName());
        newUser.setEmail(userForm.getEmail());

        // :> saving without encryption
        // newUser.setPassword(userForm.getPassword());

        // :> saving after encryption
        newUser.setPassword(passwordEncoder.encode(userForm.getPassword()));

        // :> configuring user roles
        newUser.setRoleList(List.of(AppConstants.ROLE_USER));
        logger.info(newUser.getProvider().toString());

        // :> dynamic data
        // newUser.setAbout(userForm.getAbout());

        // :> [testing] static data
        newUser.setAbout("I'm a new user.");

        newUser.setPhoneNumber(userForm.getPhoneNumber());
        newUser.setProfilePicture("https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png");

        return userRepo.save(newUser);
    }

    @Override
    public Optional<User> getUserById(String userId) {

        return userRepo.findById(userId);
    }

    @Override
    public Optional<User> updateUser(User user) {

        User fetchedUser = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // Update fields
        fetchedUser.setName(user.getName());
        fetchedUser.setEmail(user.getEmail());
        fetchedUser.setPassword(user.getPassword());
        fetchedUser.setAbout(user.getAbout());
        fetchedUser.setProfilePicture(user.getProfilePicture());
        fetchedUser.setPhoneNumber(user.getPhoneNumber());
        fetchedUser.setEnabled(user.isEnabled());
        fetchedUser.setEmailVerified(user.isEmailVerified());
        fetchedUser.setPhoneNumberVerified(user.isPhoneNumberVerified());
        fetchedUser.setProvider(user.getProvider());
        fetchedUser.setProviderUserId(user.getProviderUserId());

        // Optionally, update contacts list if necessary
        if (user.getContacts() != null) {
            fetchedUser.getContacts().clear();
            fetchedUser.getContacts().addAll(user.getContacts());
        }

        // Save updated user to the database
        User updatedUser = userRepo.save(fetchedUser);
        return Optional.of(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {

        User fetchedUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        userRepo.delete(fetchedUser);
    }

    @Override
    public boolean isUserExist(String userId) {

        User fetchedUser = userRepo.findById(userId).orElse(null);

        return fetchedUser != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {

        User fetchedUser = userRepo.findByEmail(email).orElse(null);

        return fetchedUser != null ? true : false;

    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String userEmail) {

        return userRepo.findByEmail(userEmail).orElse(null);
    }

}
