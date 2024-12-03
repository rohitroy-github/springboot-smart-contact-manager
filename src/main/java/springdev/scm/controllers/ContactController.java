package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    @RequestMapping("/add")
    public String addContactView() { 
        return "user/add_contact";
    }

}
