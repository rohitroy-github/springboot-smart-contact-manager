package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import springdev.scm.forms.UserForm;
import springdev.scm.services.UserService;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(RootController.class);

    // routing for /home
    @RequestMapping("/")
    public String indexPage() {

        // logger.info(":> [logged-in] showing_index_page");

        return "index";

    }

    // routing for /home
    @RequestMapping("/home")
    public String homePage() {

        // logger.info(":> [logged-in] showing_home_page");

        return "home";

    }

    // routing for /about
    @RequestMapping("/about")
    public String aboutPage() {

        // logger.info(":> [logged-in] showing_about_page");

        return "about";

    }

    // routing for /services
    @RequestMapping("/services")
    public String servicePage() {

        // logger.info(":> [logged-in] showing_services_page");

        return "services";

    }

    // routing for /contact
    @RequestMapping("/developer-section")
    public String contactPage() {

        // logger.info(":> [logged-in] showing_developer-section_page");

        return "about_developer";

    }

    // routing for /login
    @RequestMapping(value = "/login")
    public String loginPage() {

        // logger.info(":> [logged-in] showing_login_page");
        return "login";

    }

    @RequestMapping("/register")
    public String registerPage(Model model) {

        // logger.info(":> [logged-in] showing_register_page");

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "register";

    }

    // registration-handler
    @RequestMapping(value = "register-user", method = RequestMethod.POST)
    public String processRegistration(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {

        // logger.info(":> [logged-in] processing_new_registration");

        if (rBindingResult.hasErrors()) {
            return "register";
        } else {

            userService.registerUser(userForm);

            // logger.info(":> [logged-in] user_saved_successfully");

            session.setAttribute("message", "User registered successfully");
        }

        return "redirect:/register";
    }

}
