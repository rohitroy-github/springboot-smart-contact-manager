package springdev.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springdev.scm.forms.ContactForm;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    @RequestMapping(value = "add")
    public String addContactView(Model model) { 

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactFormData", contactForm);

        return "user/add_contact";
    }

    @RequestMapping(value = "add-new-contact", method = RequestMethod.POST)
    public String processAddNewContact() { 
        return "";
    }

}
