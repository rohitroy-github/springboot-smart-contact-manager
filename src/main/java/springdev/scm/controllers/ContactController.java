package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    @RequestMapping(value = "add")
    public String addContactView() { 
        return "user/add_contact";
    }

    @RequestMapping(value = "add-new-contact", method = RequestMethod.POST)
    public String processAddNewContact() { 
        return "";
    }

}
