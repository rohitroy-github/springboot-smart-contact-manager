package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UesrController {

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard() { 
        System.out.println("[logged-in] showing user dashboard");

        return "user/dashboard";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile() { 
        System.out.println("[logged-in] showing profile page");

        return "user/profile";
    }
}
