package springdev.scm.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;

@DataJpaTest
class ContactRepositoryTests {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserRepo userRepo;

    private User testUser;
    private Contact testContact;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("password");
        testUser = userRepo.save(testUser);

        testContact = new Contact();
        testContact.setId("contact-123");
        testContact.setName("John Doe");
        testContact.setEmail("john@example.com");
        testContact.setPhoneNumber("1234567890");
        testContact.setUser(testUser);
        testContact = contactRepo.save(testContact);
    }

    @Test
    void testSaveContact() {
        assertNotNull(testContact);
        assertEquals("contact-123", testContact.getId());
        assertEquals("John Doe", testContact.getName());
    }

    @Test
    void testFindContactById() {
        Optional<Contact> foundContact = contactRepo.findById("contact-123");

        assertTrue(foundContact.isPresent());
        assertEquals("John Doe", foundContact.get().getName());
    }

    @Test
    void testFindContactByIdNotFound() {
        Optional<Contact> foundContact = contactRepo.findById("invalid-id");

        assertFalse(foundContact.isPresent());
    }

    @Test
    void testUpdateContact() {
        testContact.setName("Jane Doe");
        Contact updatedContact = contactRepo.save(testContact);

        assertEquals("Jane Doe", updatedContact.getName());
    }

    @Test
    void testDeleteContact() {
        contactRepo.deleteById("contact-123");

        Optional<Contact> deletedContact = contactRepo.findById("contact-123");
        assertFalse(deletedContact.isPresent());
    }

    @Test
    void testFindContactByUserId() {
        Page<Contact> contacts = contactRepo.findByUserId("user-123", PageRequest.of(0, 8));

        assertNotNull(contacts);
        assertEquals(1, contacts.getTotalElements());
        assertEquals("John Doe", contacts.getContent().get(0).getName());
    }

    @Test
    void testFindByUserIdAndKeyword() {
        Page<Contact> contacts = contactRepo.findByUserIdAndKeyword("user-123", "john", PageRequest.of(0, 8));

        assertNotNull(contacts);
        assertTrue(contacts.getTotalElements() > 0);
    }

    @Test
    void testFindByUserIdAndKeywordNoMatch() {
        Page<Contact> contacts = contactRepo.findByUserIdAndKeyword("user-123", "nonexistent", PageRequest.of(0, 8));

        assertNotNull(contacts);
        assertEquals(0, contacts.getTotalElements());
    }

}
