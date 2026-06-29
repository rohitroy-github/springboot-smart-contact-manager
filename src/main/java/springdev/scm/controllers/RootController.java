package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import springdev.scm.entities.User;
import springdev.scm.helper.Helper;
import springdev.scm.services.UserService;

/*
 * Developer Utility:
 * Injects globally required logged-in user context into MVC models.
 * Use this controller advice to avoid duplicating shared user model setup.
 */

@ControllerAdvice
public class RootController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if (authentication == null) {

            LOGGER.debug("No authentication available while preparing model attributes");

            return;

        } else {

            String loggedInUserEmail = Helper.getEmailOfLoggedInUser(authentication);

            User loggedInUserData = userService.getUserByEmail(loggedInUserEmail);
            if (loggedInUserData == null) {
                LOGGER.warn("Authenticated principal not found in database for email={}", loggedInUserEmail);
                model.addAttribute("loggedInUser", null);
                model.addAttribute("numberOfContacts", 0);
                return;
            }
            model.addAttribute("loggedInUser", loggedInUserData);

            int numberOfContacts = loggedInUserData.getContacts() != null ? loggedInUserData.getContacts().size() : 0;
            model.addAttribute("numberOfContacts", numberOfContacts);
            LOGGER.debug("Attached logged-in user context: email={}, contacts={}", loggedInUserEmail, numberOfContacts);

        }

    }

}
