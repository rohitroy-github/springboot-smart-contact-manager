package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * Service implementation for contact management operations.
 * Handles CRUD operations, searching, pagination, and API-layer access to
 * contacts.
 * All save/update operations require user authentication to establish
 * ownership.
 */
@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserService userService;

    /**
     * Creates and persists a new contact associated with the authenticated user.
     * Generates a UUID for the contact ID and applies a default avatar if none
     * is provided in the form.
     *
     * @param contactForm    the form data containing contact details
     * @param authentication the current authenticated user's security context
     * @return the newly created and persisted {@link Contact} entity
     */
    @Override
    public Contact save(ContactForm contactForm, Authentication authentication) {

        Contact newContact = new Contact();
        String generatedUserId = UUID.randomUUID().toString();
        newContact.setId(generatedUserId);

        // Populate contact fields from the form
        newContact.setName(contactForm.getName());
        newContact.setEmail(contactForm.getEmail());
        newContact.setPhoneNumber(contactForm.getPhoneNumber());
        newContact.setAddress(contactForm.getAddress());
        newContact.setDescription(contactForm.getDescription());
        newContact.setWebsiteLink(contactForm.getWebsiteLink());
        newContact.setLinkedInLink(contactForm.getLinkedInLink());
        newContact.setFavourite(contactForm.isFavourite());

        // Extract and associate the authenticated user with this contact
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User associatedUser = userService.getUserByEmail(userEmail);
        newContact.setUser(associatedUser);

        // Apply default avatar if none provided
        if (contactForm.getPicture() == null || contactForm.getPicture().isEmpty()) {
            newContact.setPicture(
                    "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg");
        } else {
            newContact.setPicture(contactForm.getPicture());
        }

        LOGGER.info("Creating new contact for userEmail={} with name={}", userEmail, newContact.getName());

        return contactRepo.saveAndFlush(newContact);

    }

    /**
     * Updates an existing contact after validating ownership by the authenticated
     * user.
     * Throws {@link ResourceNotFoundException} if the contact does not exist, and
     * {@link IllegalArgumentException} if the user is not the contact owner.
     *
     * @param contactForm    the form data with updated contact details
     * @param authentication the current authenticated user's security context
     * @return the updated and persisted {@link Contact} entity
     * @throws ResourceNotFoundException if contact is not found
     * @throws IllegalArgumentException  if user is not authorized to update
     */
    @Override
    public Contact update(ContactForm contactForm, Authentication authentication) {
        // Fetch existing contact or throw an exception if not found
        Contact existingContact = contactRepo.findById(contactForm.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact not found for update: " + contactForm.getId()));

        // Validate that the logged-in user owns the contact
        String userEmail = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(userEmail);
        if (!existingContact.getUser().getUserId().equals(loggedInUser.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to update this contact.");
        }

        // Update contact fields using setters
        updateContactFields(existingContact, contactForm);

        // Save and return the updated contact
        return contactRepo.saveAndFlush(existingContact);
    }

    /**
     * Helper method to update the fields of a contact from a ContactForm.
     *
     * @param existingContact The contact to be updated.
     * @param contactForm     The form containing updated contact data.
     */
    private void updateContactFields(Contact existingContact, ContactForm contactForm) {
        existingContact.setName(contactForm.getName());
        existingContact.setEmail(contactForm.getEmail());
        existingContact.setPhoneNumber(contactForm.getPhoneNumber());
        existingContact.setAddress(contactForm.getAddress());
        existingContact.setDescription(contactForm.getDescription());
        existingContact.setWebsiteLink(contactForm.getWebsiteLink());
        existingContact.setLinkedInLink(contactForm.getLinkedInLink());
        existingContact.setFavourite(contactForm.isFavourite());
        existingContact.setPicture(contactForm.getPicture());
    }

    /**
     * Retrieves a list of all contacts.
     * 
     * @return A list of all contacts.
     */
    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    /**
     * Retrieves a contact by its ID.
     * Throws a ResourceNotFoundException if the contact does not exist.
     * 
     * @param id The ID of the contact to retrieve.
     * @return The Contact object if found.
     */
    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID: " + id));
    }

    /**
     * Deletes a contact by its ID.
     * Throws a ResourceNotFoundException if the contact does not exist.
     * 
     * @param id The ID of the contact to delete.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        // First verify the contact exists
        Contact contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID: " + id));

        LOGGER.info("Deleting contact: name={}, id={}", contact.getName(), id);

        try {
            // Remove contact from user's contact list (for orphan removal)
            if (contact.getUser() != null) {
                contact.getUser().getContacts().remove(contact);
            }

            // Delete by ID directly
            contactRepo.deleteById(id);
            // Force immediate execution
            contactRepo.flush();
            LOGGER.info("Contact deleted successfully: id={}", id);
        } catch (Exception ex) {
            LOGGER.error("Database error while deleting contact: id={}", id, ex);
            throw new RuntimeException("Unable to delete contact: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves a paginated list of contacts by the user's ID.
     * 
     * @param userId   The ID of the user whose contacts are to be retrieved.
     * @param pageable The pagination information.
     * @return A Page object containing the contacts for the specified user.
     */
    @Override
    public Page<Contact> getByUserId(String userId, Pageable pageable) {
        return contactRepo.findByUserId(userId, pageable);
    }

    /**
     * Searches for contacts by the user's ID and a keyword in a paginated format.
     * 
     * @param userId   The ID of the user whose contacts are to be searched.
     * @param keyword  The keyword to search in the contact details.
     * @param pageable The pagination information.
     * @return A Page object containing the contacts that match the search criteria.
     */
    @Override
    public Page<Contact> searchByUserAndKeyword(String userId, String keyword, Pageable pageable) {
        return contactRepo.findByUserIdAndKeyword(userId, keyword, pageable);
    }

    /**
     * Retrieves a list of contacts for a user by their API key.
     * 
     * @param userId The ID of the user whose contacts are to be retrieved.
     * @param apiKey The API key for authentication.
     * @return A list of contacts for the specified user.
     */
    @Override
    public List<Contact> getUserContactsAPI(String userId, String apiKey) {
        return contactRepo.findByUserId(userId, null).getContent();
    }

}
