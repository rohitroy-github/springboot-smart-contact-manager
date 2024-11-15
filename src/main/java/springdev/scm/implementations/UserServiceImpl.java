package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;

import springdev.scm.repositories.UserRepo;
import springdev.scm.services.UserService;
import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;
import springdev.scm.helper.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {
        // :> generating random userId for newly created user
        String generatedUserId = UUID.randomUUID().toString();
        user.setUserId(generatedUserId);

        return userRepo.save(user);
    }

    @Override
    public User registerUser(UserForm userForm) {
        // :> Generating random ID for the newly created user
        String generatedUserId = UUID.randomUUID().toString();

        // :> Creating a new User object and setting its properties
        User newUser = new User();
        newUser.setUserId(generatedUserId);
        newUser.setName(userForm.getName());
        newUser.setEmail(userForm.getEmail());
        newUser.setPassword(userForm.getPassword());
        newUser.setAbout(userForm.getAbout());
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
}
