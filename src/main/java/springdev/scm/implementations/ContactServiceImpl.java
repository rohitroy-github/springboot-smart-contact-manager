package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.Helper;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.repositories.ContactRepo;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserService userService;

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

        return contactRepo.save(newContact);

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

    // @Override
    // public List<Contact> search(String name, String email, String phoneNumber) {

    // }

    @Override
    public List<Contact> getByUserId(String userId) {

        return contactRepo.findByUserId(userId);

    }

    @Override
    public List<Contact> searchByName(String name, String email, String phoneNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByName'");
    }

}
