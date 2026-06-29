package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Controller responsible for handling all contact-related operations for
 * authenticated users, including adding, viewing, searching, editing,
 * updating, and deleting contacts.
 *
 * Base URL: /user/contact
 */
@Controller
@RequestMapping("user/contact")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    /**
     * Displays the Add New Contact form.
     * Initializes an empty {@link ContactForm} and passes it to the view.
     *
     * GET /user/contact/add
     */
    @RequestMapping(value = "add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactFormData", contactForm);
        model.addAttribute("process_type", "add_new_contact");

        logger.info(":> showing_add_new_contact_page ");

        return "user/add_contact";
    }

    /**
     * Processes the submission of the Add New Contact form.
     * Validates the form data; on success, saves the contact and redirects
     * back to the add contact page. On validation failure, returns the user
     * to the add contact view with an error message.
     *
     * POST /user/contact/save-contact
     */
    @RequestMapping(value = "save-contact", method = RequestMethod.POST)
    public String processAddNewContact(@ModelAttribute ContactForm contactForm, Authentication authentication,
            BindingResult rBindingResult,
            HttpSession session) {

        // Validate form fields before persisting
        if (rBindingResult.hasErrors()) {
            session.setAttribute("message", "Please recheck the data entered.");

            return "user/contact/add";
        } else {

            logger.info(":> processing_new_contact");

            contactService.save(contactForm, authentication);

            session.setAttribute("message", "Contact saved successfully.");

            return "redirect:/user/contact/add";
            // return "redirect:/user/all_contacts";

        }

    }

    /**
     * Displays a paginated list of contacts for the currently authenticated user.
     * Supports an optional search query to filter contacts by keyword.
     * If the user session is stale (user not found), redirects to the login page.
     *
     * GET /user/contact/all-contacts
     *
     * @param page   zero-based page index (default: 0)
     * @param size   number of contacts per page (default: 8)
     * @param search optional keyword to filter contacts
     */
    @RequestMapping(value = "all-contacts", method = RequestMethod.GET)
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model, Authentication authentication,
            HttpSession session) {

        // Get the email of the logged-in user
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userEmail);
        if (loggedInUser == null) {
            session.setAttribute("message", "Session is stale after reset. Please log in again.");
            return "redirect:/login";
        }

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

    /**
     * Deletes the contact with the given ID if it belongs to the authenticated
     * user.
     * Performs an ownership check before deletion to prevent unauthorized removal.
     * Handles {@link ResourceNotFoundException} and generic exceptions gracefully.
     *
     * GET /user/contact/delete/{id}
     *
     * @param contactId the ID of the contact to delete
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public String deleteContact(@PathVariable("id") String contactId, Authentication authentication,
            HttpSession session) {
        try {
            String userEmail = Helper.getEmailOfLoggedInUser(authentication);
            User loggedInUser = userService.getUserByEmail(userEmail);
            if (loggedInUser == null) {
                session.setAttribute("message", "Session is stale after reset. Please log in again.");
                return "redirect:/login";
            }

            // Fetch the contact to be deleted
            Contact contact = contactService.getById(contactId);

            logger.info(":> attempting_to_delete_contact_with_id: {}", contactId);

            // Ensure that the logged-in user owns this contact
            if (!contact.getUser().getUserId().equals(loggedInUser.getUserId())) {
                session.setAttribute("message", "You do not have permission to delete this contact.");
                return "redirect:/user/contact/all-contacts";
            }

            contactService.delete(contactId);
            session.setAttribute("message", "Contact deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            session.setAttribute("message", "Contact not found.");
            logger.warn("Contact not found for deletion: {}", contactId);
        } catch (Exception ex) {
            session.setAttribute("message", "Error deleting contact: " + ex.getMessage());
            logger.error("Error deleting contact with ID: {}", contactId, ex);
        }

        return "redirect:/user/contact/all-contacts";
    }

    /**
     * Displays the Edit Contact form pre-populated with the existing data
     * of the contact identified by the given ID.
     *
     * GET /user/contact/edit/{id}
     *
     * @param contactId the ID of the contact to edit
     */
    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String editContactView(@PathVariable("id") String contactId, Model model, Authentication authentication) {
        Contact contact = contactService.getById(contactId);

        model.addAttribute("contactFormData", contact);
        model.addAttribute("process_type", "update_contact");
        model.addAttribute("contactId_to_be_updated", contactId);

        return "user/add_contact";
    }

    /**
     * Processes the update of an existing contact.
     * Sets the contact ID from the path variable into the form, then delegates
     * the update logic to the service layer.
     *
     * POST /user/contact/update-contact/{id}
     *
     * @param contactId the ID of the contact to update
     */
    @RequestMapping(value = "update-contact/{id}", method = RequestMethod.POST)
    public String updateContact(@ModelAttribute ContactForm contactForm, @PathVariable("id") String contactId,
            Authentication authentication, HttpSession session) {

        logger.info("contact_id_fetched: {}", contactId);

        contactForm.setId(contactId);
        contactService.update(contactForm, authentication);
        session.setAttribute("message", "Contact updated successfully.");

        return "redirect:/user/contact/all-contacts";
    }

}