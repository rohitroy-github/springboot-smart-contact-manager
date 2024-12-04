package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.Helper;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;

@Controller
@RequestMapping("user/contact")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "add")
    public String addContactView(Model model) {

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactFormData", contactForm);

        logger.info(":> showing add new contact page");

        return "user/add_contact";
    }

    @RequestMapping(value = "save-contact", method = RequestMethod.POST)
    public String processAddNewContact(@ModelAttribute ContactForm contactForm, Authentication authentication) {

        System.out.println(contactForm);

        logger.info(":> processing new contact");

        Contact newContact = new Contact();
        newContact.setName(contactForm.getName());
        newContact.setEmail(contactForm.getEmail());
        newContact.setPhoneNumber(contactForm.getPhoneNumber());
        newContact.setAddress(contactForm.getAddress());
        newContact.setDescription(contactForm.getDescription());
        newContact.setWebsiteLink(contactForm.getWebsiteLink());
        newContact.setLinkedInLink(contactForm.getLinkedInLink());
        newContact.setFavourite(contactForm.isFavourite());

        // :> fetching user

        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User associatedUser = userService.getUserByEmail(userEmail);
        newContact.setUser(associatedUser);

        // Handle profile image upload
        // if (contactForm.getProfileImage() != null &&
        // !contactForm.getProfileImage().isEmpty()) {
        // // Assuming a service for file upload
        // String uploadedFilePath =
        // fileUploadService.uploadFile(contactForm.getProfileImage());
        // newContact.setPicture(uploadedFilePath);
        // }

        newContact.setPicture("https://avatars.githubusercontent.com/u/68563695?v=4");

        contactService.save(newContact);

        return "redirect:/user/contact/add";
    }

}
