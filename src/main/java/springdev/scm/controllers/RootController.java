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

    private Logger logger = LoggerFactory.getLogger(UesrController.class);

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if (authentication == null) {

            return;

        }

        String loggedInUserEmail = Helper.getEmailOfLoggedInUser(authentication);

        User loggedInUserData = userService.getUserByEmail(loggedInUserEmail);

        model.addAttribute("loggedInUser", loggedInUserData);

        logger.info(":> [{}] > user_logged_in", loggedInUserEmail);

    }

}
