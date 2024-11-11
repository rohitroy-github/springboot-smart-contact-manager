package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String home(Model model) { 

        System.out.println("Home Page");
        model.addAttribute("name", "Rohit");
        return "home";

    }

}
