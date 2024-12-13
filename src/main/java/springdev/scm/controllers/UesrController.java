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

@Controller
@RequestMapping("/user")
public class UesrController {

    private Logger logger = LoggerFactory.getLogger(UesrController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard() {
        logger.info(":> [logged-in] showing_user_dashboard");

        return "user/dashboard";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication) {

        logger.info(":> [logged-in] showing_user_profile");

        return "user/profile";
    }

    @RequestMapping(value = "profile/edit/{userId}", method = RequestMethod.GET)
    public String editUserProfile(@PathVariable("userId") String userId, Model model, Authentication authentication) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

            model.addAttribute("loggedInUser", user);
        } catch (Exception e) {
            logger.error("Error while fetching user details: " + e.getMessage(), e);
            model.addAttribute("error", "An error occurred while fetching your profile. Please try again later.");
        }

        return "user/edit_profile";
    }

    @RequestMapping(value = "profile/edit/{userId}", method = RequestMethod.POST)
    public String updateUserProfile(
            @PathVariable("userId") String userId,
            @ModelAttribute("userForm") UserForm userForm,
            Model model,
            HttpSession session) {
        try {
            Optional<User> optionalUser = userService.getUserById(userId);

            if (optionalUser.isPresent()) {
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
            logger.error("Error verifying email: " + e.getMessage(), e);
            session.setAttribute("message", "An error occurred while verifying email.");
        }

        return "redirect:/user/profile";
    }

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
            logger.error("Error verifying phone number: " + e.getMessage(), e);
            session.setAttribute("message", "An error occurred while verifying phone number.");
        }

        return "redirect:/user/profile";
    }

    

}
