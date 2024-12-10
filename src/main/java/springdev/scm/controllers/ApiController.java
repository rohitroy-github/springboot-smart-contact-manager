package springdev.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springdev.scm.entities.Contact;
import springdev.scm.services.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    private Logger logger = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {

        logger.info("fetching_contact_info : {}", contactId);

        Contact contact = contactService.getById(contactId);
        logger.info("Contact fetched: {}", contact);

        return contactService.getById(contactId);

    }

}
