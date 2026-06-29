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

/**
 * Controller responsible for serving all public-facing static pages and
 * handling new user registration. No authentication is required for these
 * routes.
 */
@Controller
public class PageController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(PageController.class);

    /**
     * Serves the application root. Redirects to the home page.
     *
     * GET /
     */
    @RequestMapping("/")
    public String indexPage() {
        logger.info(":> showing_index_page");
        return "home";
    }

    /**
     * Displays the home page.
     *
     * GET /home
     */
    @RequestMapping("/home")
    public String homePage() {
        logger.info(":> showing_home_page");
        return "home";
    }

    /**
     * Displays the about page.
     *
     * GET /about
     */
    @RequestMapping("/about")
    public String aboutPage() {
        logger.info(":> showing_about_page");
        return "about";
    }

    /**
     * Displays the services page.
     *
     * GET /services
     */
    @RequestMapping("/services")
    public String servicePage() {
        logger.info(":> showing_services_page");
        return "services";
    }

    /**
     * Displays the developer section / about-the-developer page.
     *
     * GET /developer-section
     */
    @RequestMapping("/developer-section")
    public String contactPage() {
        logger.info(":> showing_developer_section_page");
        return "about_developer";
    }

    /**
     * Displays the login page.
     *
     * GET /login
     */
    @RequestMapping(value = "/login")
    public String loginPage() {
        logger.info(":> showing_login_page");
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
        logger.info(":> showing_register_page");

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

        logger.info(":> processing_new_registration");

        if (rBindingResult.hasErrors()) {
            return "register";
        } else {
            userService.registerUser(userForm);
            logger.info(":> user_saved_successfully");
            session.setAttribute("message", "User registered successfully");
        }

        return "redirect:/register";
    }

}
