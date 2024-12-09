package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import springdev.scm.controllers.ContactController;
import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.Helper;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.repositories.ContactRepo;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Override
    public Contact save(ContactForm contactForm, Authentication authentication) {

        Contact newContact = new Contact();
        String generatedUserId = UUID.randomUUID().toString();
        newContact.setId(generatedUserId);

        newContact.setName(contactForm.getName());
        newContact.setEmail(contactForm.getEmail());
        newContact.setPhoneNumber(contactForm.getPhoneNumber());
        newContact.setAddress(contactForm.getAddress());
        newContact.setDescription(contactForm.getDescription());
        newContact.setWebsiteLink(contactForm.getWebsiteLink());
        newContact.setLinkedInLink(contactForm.getLinkedInLink());
        newContact.setFavourite(contactForm.isFavourite());

        // :> extracting User from Authentication
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User associatedUser = userService.getUserByEmail(userEmail);
        newContact.setUser(associatedUser);

        // if (contactForm.getContactImage() != null &&
        // !contactForm.getContactImage().isEmpty()) {

        // logger.info("uploaded_file : {}",
        // contactForm.getContactImage().getOriginalFilename());

        // // :> fucntionality to store image in cloud and fetching the URL
        // }

        // newContact.setPicture("https://avatars.githubusercontent.com/u/68563695?v=4");

        if (contactForm.getPicture() == null || contactForm.getPicture().isEmpty()) {
            newContact.setPicture(
                    "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg");
        } else {
            newContact.setPicture(contactForm.getPicture());
        }

        logger.info("data : {}", newContact);

        return contactRepo.save(newContact);

        // return newContact;

    }

    @Override
    public List<Contact> getAll() {

        return contactRepo.findAll();

    }

    @Override
    public Contact getById(String id) {

        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID:" + id));

    }

    @Override
    public void delete(String id) {

        Contact contactToBeDeleted = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID:" + id));
        contactRepo.delete(contactToBeDeleted);

    }

    @Override
    public Page<Contact> getByUserId(String userId, Pageable pageable) {

        return contactRepo.findByUserId(userId, pageable);

    }

    @Override
    public Page<Contact> searchByUserAndKeyword(String userId, String keyword, Pageable pageable) {
        return contactRepo.findByUserIdAndKeyword(userId, keyword, pageable);
    }

    @Override
    public Contact update(ContactForm contactForm, Authentication authentication) {
        Contact existingContact = contactRepo.findById(contactForm.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for update: " + contactForm.getId()));

        // Ensure that the contact belongs to the logged-in user
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userEmail);

        if (!existingContact.getUser().getUserId().equals(loggedInUser.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to update this contact.");
        }

        existingContact.setName(contactForm.getName());
        existingContact.setEmail(contactForm.getEmail());
        existingContact.setPhoneNumber(contactForm.getPhoneNumber());
        existingContact.setAddress(contactForm.getAddress());
        existingContact.setDescription(contactForm.getDescription());
        existingContact.setWebsiteLink(contactForm.getWebsiteLink());
        existingContact.setLinkedInLink(contactForm.getLinkedInLink());
        existingContact.setFavourite(contactForm.isFavourite());
        existingContact.setPicture(contactForm.getPicture());

        return contactRepo.save(existingContact);
    }

}
