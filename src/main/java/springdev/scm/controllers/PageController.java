package springdev.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springdev.scm.entities.User;
import springdev.scm.forms.UserForm;
import springdev.scm.services.UserService;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    // routing for /home
    @RequestMapping("/home")
    public String homePage(Model model) {

        System.out.println("Home Page");
        model.addAttribute("name", "Rohit");
        return "home";

    }

    // routing for /about
    @RequestMapping("/about")
    public String aboutPage(Model model) {

        System.out.println("About Page");
        model.addAttribute("name", "Rohit");
        return "about";

    }

    // routing for /services
    @RequestMapping("/services")
    public String servicePage(Model model) {

        System.out.println("Service Page");
        model.addAttribute("name", "Rohit");
        return "services";

    }

    // routing for /contact
    @RequestMapping("/contact")
    public String contactPage(Model model) {

        System.out.println("Contact Page");
        model.addAttribute("name", "Rohit");
        return "services";

    }

    // routing for /contact
    @RequestMapping("/login")
    public String loginPage(Model model) {

        System.out.println("Login Page");
        model.addAttribute("name", "Rohit");
        return "login";

    }

    // routing for /contact
    @RequestMapping("/register")
    public String registerPage(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        System.out.println("Register Page"); 
        model.addAttribute("name", "Rohit");
        return "register";

    }

    // registration-handler
    @RequestMapping(value = "register-user", method = RequestMethod.POST)
    public String processRegistration(@ModelAttribute UserForm userForm) { 

        // :> building User object from 'userForm' data
        User builtUser = User.builder()
        .name(userForm.getName())
        .email(userForm.getEmail())
        .password(userForm.getPassword())
        .about(userForm.getAbout())
        .phoneNumber(userForm.getPhoneNumber())
        .profilePicture("https://www.pngarts.com/files/10/Default-Profile-Picture-Download-PNG-Image.png")
        .build();

        User savedUser = userService.saveUser(builtUser);

        System.out.println("User saved successfully.");

        return "redirect:/register";
    }

}
