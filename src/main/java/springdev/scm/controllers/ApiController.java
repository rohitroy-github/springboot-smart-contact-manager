package springdev.scm.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springdev.scm.entities.Contact;
import springdev.scm.services.ContactService;

// :> fetching values from application.properties
import org.springframework.beans.factory.annotation.Value;

/**
 * REST controller that exposes public API endpoints for contact data retrieval.
 * API key-based authentication is used to protect user-scoped endpoints.
 *
 * Base URL: /api
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    /**
     * API key loaded from application.properties, used to authenticate requests.
     */
    @Value("${api.key}")
    private String API_KEY;

    private Logger logger = LoggerFactory.getLogger(ApiController.class);

    /**
     * Retrieves the full details of a single contact by its ID.
     *
     * GET /api/contacts/{contactId}
     *
     * @param contactId the UUID of the contact to fetch
     * @return the {@link Contact} entity matching the given ID
     */
    @GetMapping("contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {

        logger.info("fetching_contact_info : {}", contactId);

        return contactService.getById(contactId);

    }

    /**
     * Retrieves all contacts belonging to a specific user.
     * Requires a valid API key passed as a query parameter; returns 401 if the
     * key is invalid. Returns 500 on unexpected service errors.
     *
     * GET /api/user/contacts?userId={userId}&apiKey={apiKey}
     *
     * @param userId the ID of the user whose contacts are to be fetched
     * @param apiKey the API key to authenticate the request
     * @return 200 with the contact list, 401 on bad key, or 500 on error
     */
    @GetMapping("/user/contacts")
    public ResponseEntity<?> getUserContacts(
            @RequestParam("userId") String userId,
            @RequestParam("apiKey") String apiKey) {

        logger.info("Fetching contacts for userId: {}", userId);

        // Reject the request immediately if the provided API key does not match
        if (!apiKey.equals(API_KEY)) {
            logger.warn("invalid_api_key_provided: {}", apiKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API key.");
        }

        try {
            List<Contact> contacts = contactService.getUserContactsAPI(userId, apiKey);
            logger.info("Fetched {} contacts for userId: {}", contacts.size(), userId);
            return ResponseEntity.ok(contacts);

        } catch (Exception e) {
            logger.error("Error fetching contacts for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching contacts.");
        }
    }

}
