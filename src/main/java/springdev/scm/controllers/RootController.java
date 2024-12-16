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

@ControllerAdvice
public class RootController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(RootController.class);

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if (authentication == null) {

            return;

        } else {

            String loggedInUserEmail = Helper.getEmailOfLoggedInUser(authentication);

            User loggedInUserData = userService.getUserByEmail(loggedInUserEmail);
            model.addAttribute("loggedInUser", loggedInUserData);

            // logger.info(":> [{}] > user_logged_in", loggedInUserEmail);

            int numberOfContacts = loggedInUserData.getContacts() != null ? loggedInUserData.getContacts().size() : 0;
            model.addAttribute("numberOfContacts", numberOfContacts);

            // logger.info(":> [{}] > user_logged_in with [{}] contact(s)",
            // loggedInUserEmail, numberOfContacts);

        }

    }

}
