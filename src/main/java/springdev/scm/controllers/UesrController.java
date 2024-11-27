package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springdev.scm.helper.Helper;

@Controller
@RequestMapping("/user")
public class UesrController {

    private Logger logger = LoggerFactory.getLogger(UesrController.class);

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard() {
        System.out.println(":> [logged-in] showing user dashboard");

        return "user/dashboard";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Authentication authentication) {

        String loggedInUserEmail = Helper.getEmailOfLoggedInUser(authentication);

        logger.info(":> [{}] > showing profile page", loggedInUserEmail);

        return "user/profile";
    }
}
