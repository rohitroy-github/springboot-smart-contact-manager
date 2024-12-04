package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import springdev.scm.entities.Contact;
import springdev.scm.forms.ContactForm;
import springdev.scm.services.ContactService;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @RequestMapping(value = "add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactFormData", contactForm);

        logger.info(":> showing add new contact page");

        return "user/add_contact";
    }

    @RequestMapping(value = "save-contact", method = RequestMethod.POST)
    public String processAddNewContact(@ModelAttribute ContactForm contactForm, Authentication authentication,
            BindingResult rBindingResult,
            HttpSession session) {

        System.out.println(contactForm);

        if (rBindingResult.hasErrors()) {
            return "user/contact/add";
        } else {

            logger.info(":> processing_new_contact");

            Contact savedContact = contactService.save(contactForm, authentication);

            session.setAttribute("message", "Contact saved successfully.");

            return "redirect:/user/contact/add";

        }

    }

}
