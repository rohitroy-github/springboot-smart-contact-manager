package springdev.scm.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.Helper;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;

// :> pagination modules
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private UserService userService;

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
            session.setAttribute("message", "Please recheck the data entered.");

            return "user/contact/add";
        } else {

            logger.info(":> processing_new_contact");

            Contact savedContact = contactService.save(contactForm, authentication);

            session.setAttribute("message", "Contact saved successfully.");

            return "redirect:/user/contact/add";

        }

    }

    @RequestMapping(value = "all-contacts", method = RequestMethod.GET)
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model, Authentication authentication) {

        // Get the email of the logged-in user
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userEmail);

        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> contacts;


        if (search != null && !search.isEmpty()) {
            // Search for contacts based on the query
            contacts = contactService.searchByUserAndKeyword(loggedInUser.getUserId(), search, pageable);
        } else {
            // Fetch all contacts if no search query is provided
            contacts = contactService.getByUserId(loggedInUser.getUserId(), pageable);

        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("search", search);

        logger.info(":> showing all contacts for user: {}", loggedInUser.getName());

        return "user/all_contacts";
    }

}
