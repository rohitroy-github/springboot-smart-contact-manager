package springdev.scm.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.services.UserService;

/*
 * Developer Utility:
 * Manages authenticated user account screens and profile verification actions.
 * Use this controller for /user dashboard/profile/edit/verify workflows.
 */

/**
 * Controller responsible for handling user-specific pages and profile
 * management
 * for authenticated users. All routes are prefixed with /user.
 *
 * Covers: dashboard, profile view, profile editing, and contact/email
 * verification.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Displays the authenticated user's dashboard.
     *
     * GET /user/dashboard
     */
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard() {
        return "user/dashboard";
    }

    /**
     * Displays the authenticated user's profile page.
     *
     * GET /user/profile
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication) {
        return "user/profile";
    }

    /**
     * Displays the Edit Profile form pre-populated with the user's current data.
     * Throws {@link ResourceNotFoundException} if no user exists for the given ID.
     *
     * GET /user/profile/edit/{userId}
     *
     * @param userId the ID of the user whose profile is to be edited
     */
    @RequestMapping(value = "profile/edit/{userId}", method = RequestMethod.GET)
    public String editUserProfile(@PathVariable("userId") String userId, Model model, Authentication authentication) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

            model.addAttribute("loggedInUser", user);
        } catch (Exception e) {
            LOGGER.error("Error while fetching profile details for userId={}", userId, e);
            model.addAttribute("error", "An error occurred while fetching your profile. Please try again later.");
        }

        return "user/edit_profile";
    }

    /**
     * Processes the Edit Profile form submission.
     * If the user exists, applies the updated data via the service layer and
     * redirects to the profile page. Returns the edit form on failure.
     *
     * POST /user/profile/edit/{userId}
     *
     * @param userId   the ID of the user to update
     * @param userForm the form data containing updated user fields
     */
    @RequestMapping(value = "profile/edit/{userId}", method = RequestMethod.POST)
    public String updateUserProfile(
            @PathVariable("userId") String userId,
            @ModelAttribute("userForm") UserForm userForm,
            Model model,
            HttpSession session) {
        try {
            Optional<User> optionalUser = userService.getUserById(userId);

            if (optionalUser.isPresent()) {
                // Inject the path variable ID into the form before updating
                userForm.setId(userId);

                userService.updateUser(userForm);

                session.setAttribute("message", "User updated successfully.");

                return "redirect:/user/profile";
            } else {
                session.setAttribute("message", "User not found.");
                return "user/edit-profile";
            }
        } catch (Exception e) {
            session.setAttribute("message", "Failed to updated user.");
            return "user/edit-profile";
        }
    }

    /**
     * Verifies the email address of the user identified by the given ID.
     * Delegates verification logic to the service layer and sets a session
     * message indicating success or failure.
     *
     * GET /user/verify/email/{userId}
     *
     * @param userId the ID of the user whose email is to be verified
     */
    @RequestMapping(value = "/verify/email/{userId}", method = RequestMethod.GET)
    public String verifyEmail(@PathVariable("userId") String userId, HttpSession session) {
        try {
            boolean success = userService.verifyEmail(userId);

            if (success) {
                session.setAttribute("message", "Email verified successfully.");
            } else {
                session.setAttribute("message", "Failed to verify email.");
            }
        } catch (Exception e) {
            LOGGER.error("Error verifying email for userId={}", userId, e);
            session.setAttribute("message", "An error occurred while verifying email.");
        }

        return "redirect:/user/profile";
    }

    /**
     * Verifies the phone number of the user identified by the given ID.
     * Delegates verification logic to the service layer and sets a session
     * message indicating success or failure.
     *
     * GET /user/verify/phone/{userId}
     *
     * @param userId the ID of the user whose phone number is to be verified
     */
    @RequestMapping(value = "/verify/phone/{userId}", method = RequestMethod.GET)
    public String verifyPhone(@PathVariable("userId") String userId, HttpSession session) {
        try {
            boolean success = userService.verifyPhone(userId);

            if (success) {
                session.setAttribute("message", "Phone number verified successfully.");
            } else {
                session.setAttribute("message", "Failed to verify phone number.");
            }
        } catch (Exception e) {
            LOGGER.error("Error verifying phone number for userId={}", userId, e);
            session.setAttribute("message", "An error occurred while verifying phone number.");
        }

        return "redirect:/user/profile";
    }

}
