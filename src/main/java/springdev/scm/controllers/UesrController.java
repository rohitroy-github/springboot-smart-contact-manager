package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import springdev.scm.entities.User;
import springdev.scm.helper.Helper;
import springdev.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UesrController {

    private Logger logger = LoggerFactory.getLogger(UesrController.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard() {
        logger.info(":> [logged-in] showing user dashboard");

        return "user/dashboard";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication) {

        logger.info(":> [logged-in] showing user profile");

        return "user/profile";
    }
}
