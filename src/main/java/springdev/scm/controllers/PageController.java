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

/*
 * Developer Utility:
 * Handles public entry pages and signup flow for unauthenticated users.
 * Use this controller for static/public routes like home, about, login, and register.
 */

/**
 * Controller responsible for serving all public-facing static pages and
 * handling new user registration. No authentication is required for these
 * routes.
 */
@Controller
public class PageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private UserService userService;

    /**
     * Serves the application root. Redirects to the home page.
     *
     * GET /
     */
    @RequestMapping("/")
    public String indexPage() {
        LOGGER.info("Serving landing page: GET /");
        return "home";
    }

    /**
     * Displays the home page.
     *
     * GET /home
     */
    @RequestMapping("/home")
    public String homePage() {
        LOGGER.info("Serving page: GET /home");
        return "home";
    }

    /**
     * Displays the about page.
     *
     * GET /about
     */
    @RequestMapping("/about")
    public String aboutPage() {
        LOGGER.info("Serving page: GET /about");
        return "about";
    }

    /**
     * Displays the services page.
     *
     * GET /services
     */
    @RequestMapping("/services")
    public String servicePage() {
        LOGGER.info("Serving page: GET /services");
        return "services";
    }

    /**
     * Displays the developer section / about-the-developer page.
     *
     * GET /developer-section
     */
    @RequestMapping("/developer-section")
    public String contactPage() {
        LOGGER.info("Serving page: GET /developer-section");
        return "about_developer";
    }

    /**
     * Displays the login page.
     *
     * GET /login
     */
    @RequestMapping(value = "/login")
    public String loginPage() {
        LOGGER.info("Serving page: GET /login");
        return "login";
    }

    /**
     * Displays the registration page with an empty {@link UserForm} bound to the
     * view.
     *
     * GET /register
     */
    @RequestMapping("/register")
    public String registerPage(Model model) {
        LOGGER.info("Serving page: GET /register");

        // Initialise an empty form object for Thymeleaf binding
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "register";
    }

    /**
     * Processes the new user registration form.
     * Validates the submitted {@link UserForm}; on failure returns the register
     * view with binding errors. On success, persists the user and redirects back
     * to the registration page with a confirmation message.
     *
     * POST /register-user
     *
     * @param userForm       the form data submitted by the user
     * @param rBindingResult validation result for the form
     */
    @RequestMapping(value = "register-user", method = RequestMethod.POST)
    public String processRegistration(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {

        LOGGER.info("Processing registration request: POST /register-user");

        if (rBindingResult.hasErrors()) {
            LOGGER.warn("Registration validation failed with {} error(s)", rBindingResult.getErrorCount());
            return "register";
        } else {
            userService.registerUser(userForm);
            LOGGER.info("User registration completed successfully");
            session.setAttribute("message", "User registered successfully");
        }

        return "redirect:/register";
    }

}
