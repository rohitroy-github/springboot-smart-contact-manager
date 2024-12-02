package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/user")
public class UesrController {

    private Logger logger = LoggerFactory.getLogger(UesrController.class);


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
}
