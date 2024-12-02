package springdev.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;
import springdev.scm.services.UserService;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    // routing for /home
    @RequestMapping("/")
    public String indexPage() {

        System.out.println(":> showing index page");
        return "index";

    }

    // routing for /home
    @RequestMapping("/home")
    public String homePage(Model model) {

        System.out.println(":> showing home page");
        model.addAttribute("name", "Rohit");
        return "home";

    }

    // routing for /about
    @RequestMapping("/about")
    public String aboutPage(Model model) {

        System.out.println(":> showing about page");
        model.addAttribute("name", "Rohit");
        return "about";

    }

    // routing for /services
    @RequestMapping("/services")
    public String servicePage(Model model) {

        System.out.println(":> showing service page");
        model.addAttribute("name", "Rohit");
        return "services";

    }

    // routing for /contact
    @RequestMapping("/contact")
    public String contactPage(Model model) {

        System.out.println(":> showing contact page");
        model.addAttribute("name", "Rohit");
        return "services";

    }

    // routing for /login
    @RequestMapping(value = "/login")
    public String loginPage() {

        System.out.println(":> showing login page");
        return "login";

    }

    // @RequestMapping(value = "/login-user", method = RequestMethod.POST) 
    // public String processLogin() {

    //     return "redirect:/user/profile";

    // } 

    @RequestMapping("/register")
    public String registerPage(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        System.out.println(":> showing registration page");
        model.addAttribute("name", "Rohit");
        return "register";

    }

    // registration-handler
    @RequestMapping(value = "register-user", method = RequestMethod.POST)
    public String processRegistration(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {

        System.out.println(":> processing new registration");

        if (rBindingResult.hasErrors()) {
            return "register";
        } else {

            User savedUser = userService.registerUser(userForm);
            System.out.println(":> user saved successfully");

            session.setAttribute("message", "User registered successfully");
        }

        return "redirect:/register";
    }

}
