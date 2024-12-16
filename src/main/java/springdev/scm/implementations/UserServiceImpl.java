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

    /**
     * Creates a base User object with common attributes for all OAuth providers.
     *
     * @param oAuthenticatedUser The OAuth2 authenticated user details.
     * @param oAuthClient        The OAuth client (e.g., Google, Github).
     * @return A partially initialized User object.
     */
    private User createBaseUser(DefaultOAuth2User oAuthenticatedUser, String oAuthClient) {
        User user = new User();

        user.setUserId(UUID.randomUUID().toString());
        user.setName(getAttribute(oAuthenticatedUser, "name"));
        user.setEmail(getAttribute(oAuthenticatedUser, "email"));
        user.setPassword(passwordEncoder.encode("oauth_default_password"));
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setProviderUserId(oAuthenticatedUser.getName());

        return user;
    }

    /**
     * Helper method to fetch attributes from the OAuth2User object.
     */
    private String getAttribute(DefaultOAuth2User oAuthenticatedUser, String attribute) {
        return oAuthenticatedUser.getAttribute(attribute) != null
                ? oAuthenticatedUser.getAttribute(attribute).toString()
                : "";
    }

    @Override
    public User saveOAuthenticatedUser(DefaultOAuth2User oAuthenticatedUser, String oAuthClient) {
        User newUser = createBaseUser(oAuthenticatedUser, oAuthClient);

        // Add provider-specific attributes
        switch (oAuthClient.toLowerCase()) {
            case "google":
                newUser.setProfilePicture(getAttribute(oAuthenticatedUser, "picture"));
                newUser.setAbout("I'm a new user authenticated using Google OAuth2.");
                newUser.setProvider(Providers.GOOGLE);
                break;
            case "github":
                newUser.setProfilePicture(getAttribute(oAuthenticatedUser, "avatar_url"));
                newUser.setAbout("I'm a new user authenticated using Github OAuth2.");
                newUser.setProvider(Providers.GITHUB);
                break;
            default:
                throw new IllegalArgumentException("Unsupported OAuth provider: " + oAuthClient);
        }

        return userRepo.save(newUser);
    }

    /**
     * Saves a new user with default attributes for manual registration.
     */
    @Override
    public User saveUser(User user) {
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        return userRepo.save(user);
    }

    /**
     * Registers a new user based on the provided UserForm.
     *
     * @param userForm The form containing user registration details.
     * @return The newly registered User.
     */
    @Override
    public User registerUser(UserForm userForm) {
        User newUser = new User();

        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setName(userForm.getName());
        newUser.setEmail(userForm.getEmail());
        newUser.setPassword(passwordEncoder.encode(userForm.getPassword()));
        newUser.setRoleList(List.of(AppConstants.ROLE_USER));
        // Inside registerUser or wherever you're setting the 'about' field:
        String about = userForm.getAbout();
        newUser.setAbout(about != null && !about.trim().isEmpty() ? about : "I'm a new user.");

        newUser.setPhoneNumber(userForm.getPhoneNumber());
        newUser.setProfilePicture("https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png");

        return userRepo.save(newUser);
    }

    /**
     * Updates an existing user based on the provided UserForm.
     *
     * @param userForm The form containing updated user details.
     * @return The updated User.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    @Override
    public User updateUser(UserForm userForm) {
        User existingUser = userRepo.findById(userForm.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userForm.getId()));

        // Update the fields
        updateUserFields(existingUser, userForm);

        return userRepo.save(existingUser);
    }

    /**
     * Helper method to update fields of an existing user from a UserForm.
     *
     * @param existingUser The user to be updated.
     * @param userForm     The form containing updated details.
     */
    private void updateUserFields(User existingUser, UserForm userForm) {
        existingUser.setName(userForm.getName());
        existingUser.setEmail(userForm.getEmail());
        existingUser.setPhoneNumber(userForm.getPhoneNumber());
        existingUser.setAbout(userForm.getAbout());
        existingUser.setProfilePicture(userForm.getProfilePicture());
    }

    @Override
    public Optional<User> getUserById(String userId) {

        return userRepo.findById(userId);
    }

    /**
     * Deletes a user by their ID. Throws a ResourceNotFoundException if the user
     * does not exist.
     * 
     * @param userId The ID of the user to delete.
     */
    @Override
    public void deleteUser(String userId) {
        User fetchedUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        userRepo.delete(fetchedUser);
    }

    /**
     * Checks if a user exists by their ID.
     * 
     * @param userId The ID of the user to check.
     * @return true if the user exists, false otherwise.
     */
    @Override
    public boolean isUserExist(String userId) {
        User fetchedUser = userRepo.findById(userId).orElse(null);
        return fetchedUser != null;
    }

    /**
     * Checks if a user exists by their email address.
     * 
     * @param email The email address of the user to check.
     * @return true if the user exists, false otherwise.
     */
    @Override
    public boolean isUserExistByEmail(String email) {
        User fetchedUser = userRepo.findByEmail(email).orElse(null);
        return fetchedUser != null;
    }

    /**
     * Retrieves a list of all users.
     * 
     * @return A list of all users.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Retrieves a user by their email address.
     * 
     * @param userEmail The email address of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    @Override
    public User getUserByEmail(String userEmail) {
        return userRepo.findByEmail(userEmail).orElse(null);
    }

    /**
     * Verifies the email address of a user by their ID.
     * 
     * @param userId The ID of the user whose email is to be verified.
     * @return true if the email was successfully verified, false otherwise.
     */
    @Override
    public boolean verifyEmail(String userId) {
        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmailVerified(true);
            userRepo.save(user);
            return true;
        }

        return false;
    }

    /**
     * Verifies the phone number of a user by their ID.
     * 
     * @param userId The ID of the user whose phone number is to be verified.
     * @return true if the phone number was successfully verified, false otherwise.
     */
    @Override
    public boolean verifyPhone(String userId) {
        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPhoneNumberVerified(true);
            userRepo.save(user);
            return true;
        }

        return false;
    }

}
