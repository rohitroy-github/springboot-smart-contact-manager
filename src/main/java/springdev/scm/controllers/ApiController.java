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

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    // private static final String API_KEY = "131bfbc7-f12b-43e2-b42c-b9c7e1653247";

    @Value("${api.key}")
    private String API_KEY;

    private Logger logger = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {

        logger.info("fetching_contact_info : {}", contactId);

        Contact contact = contactService.getById(contactId);
        logger.info("Contact fetched: {}", contact);

        return contactService.getById(contactId);

    }

    // API endpoint to fetch all the contacts of any user based on the user-id
    // /api/contacts?userId=<userId>&apiKey=<your-unique-api-key>
    // [testing] > http://localhost:8081/api/contacts?userId=7c2c4824-5f59-4bee-85d3-9ffbea76f3d0&apiKey=131bfbc7-f12b-43e2-b42c-b9c7e1653247
    @GetMapping("/contacts")
    public ResponseEntity<?> getUserContacts(
            @RequestParam("userId") String userId,
            @RequestParam("apiKey") String apiKey) {

        logger.info("Fetching contacts for userId: {}", userId);

        // Validate API key
        if (!apiKey.equals(API_KEY)) {
            logger.warn("Invalid API key provided: {}", apiKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API key.");
        }

        try {
            // Fetch contacts for the user
            List<Contact> contacts = contactService.getByUserId(userId, null).getContent();
            logger.info("Fetched {} contacts for userId: {}", contacts.size(), userId);
            return ResponseEntity.ok(contacts);

        } catch (Exception e) {
            logger.error("Error fetching contacts for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching contacts.");
        }
    }

}
