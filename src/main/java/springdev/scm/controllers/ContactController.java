package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.Helper;
import springdev.scm.helper.ResourceNotFoundException;
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

    // :> controller: add a new contact
    @RequestMapping(value = "add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactFormData", contactForm);
        model.addAttribute("process_type", "add_new_contact");

        logger.info(":> showing add new contact page");

        return "user/add_contact";
    }

    // :> controller: save a new contact
    @RequestMapping(value = "save-contact", method = RequestMethod.POST)
    public String processAddNewContact(@ModelAttribute ContactForm contactForm, Authentication authentication,
            BindingResult rBindingResult,
            HttpSession session) {

        // System.out.println(contactForm);

        if (rBindingResult.hasErrors()) {
            session.setAttribute("message", "Please recheck the data entered.");

            return "user/contact/add";
        } else {

            logger.info(":> processing_new_contact");

            contactService.save(contactForm, authentication);

            session.setAttribute("message", "Contact saved successfully.");

            return "redirect:/user/contact/add";

        }

    }

    // :> controller: view all contacts
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

    // :> controller: delete the contact
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String deleteContact(@PathVariable("id") String contactId, Authentication authentication,
            HttpSession session) {
        try {
            String userEmail = Helper.getEmailOfLoggedInUser(authentication);
            User loggedInUser = userService.getUserByEmail(userEmail);

            // Fetch the contact to be deleted
            Contact contact = contactService.getById(contactId);

            // Ensure that the logged-in user owns this contact
            if (!contact.getUser().getUserId().equals(loggedInUser.getUserId())) {
                session.setAttribute("message", "You do not have permission to delete this contact.");
                return "redirect:/user/contact/all-contacts";
            }

            contactService.delete(contactId);
            session.setAttribute("message", "Contact deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            session.setAttribute("message", "Contact not found.");
        }

        return "redirect:/user/contact/all-contacts";
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String editContactView(@PathVariable("id") String contactId, Model model, Authentication authentication) {
        Contact contact = contactService.getById(contactId);

        model.addAttribute("contactFormData", contact);
        model.addAttribute("process_type", "update_contact");
        model.addAttribute("contactId_to_be_updated", contactId);

        return "user/add_contact";
    }

    @RequestMapping(value = "update-contact/{id}", method = RequestMethod.POST)
    public String updateContact(@ModelAttribute ContactForm contactForm, @PathVariable("id") String contactId,
            Authentication authentication, HttpSession session) {

        logger.info("contact_id_fetched", contactId);

        contactForm.setId(contactId);
        contactService.update(contactForm, authentication);
        session.setAttribute("message", "Contact updated successfully.");

        return "redirect:/user/contact/all-contacts";
    }

}