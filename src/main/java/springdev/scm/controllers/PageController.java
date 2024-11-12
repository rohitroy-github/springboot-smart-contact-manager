package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String homePage(Model model) { 

        System.out.println("Home Page");
        model.addAttribute("name", "Rohit");
        return "home";

    }

    @RequestMapping("/about")
    public String aboutPage(Model model) { 

        System.out.println("About Page");
        model.addAttribute("name", "Rohit");
        return "about";

    }

    @RequestMapping("/services")
    public String servicePage(Model model) { 

        System.out.println("Service Page");
        model.addAttribute("name", "Rohit");
        return "services";

    }

}
