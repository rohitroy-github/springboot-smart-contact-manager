package springdev.scm.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactEntityTests {

    private Contact contact;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user-123");
        user.setEmail("test@example.com");
        user.setName("Test User");

        contact = new Contact();
        contact.setId("contact-123");
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhoneNumber("1234567890");
        contact.setAddress("123 Main St");
        contact.setDescription("Test Contact");
        contact.setFavourite(false);
        contact.setWebsiteLink("https://johndoe.com");
        contact.setLinkedInLink("https://linkedin.com/in/johndoe");
        contact.setUser(user);
    }

    @Test
    void testContactCreation() {
        assertNotNull(contact);
        assertEquals("contact-123", contact.getId());
        assertEquals("John Doe", contact.getName());
    }

    @Test
    void testContactEmail() {
        assertEquals("john@example.com", contact.getEmail());
        contact.setEmail("jane@example.com");
        assertEquals("jane@example.com", contact.getEmail());
    }

    @Test
    void testContactPhoneNumber() {
        assertEquals("1234567890", contact.getPhoneNumber());
    }

    @Test
    void testContactAddress() {
        assertEquals("123 Main St", contact.getAddress());
    }

    @Test
    void testContactFavourite() {
        assertFalse(contact.isFavourite());
        contact.setFavourite(true);
        assertTrue(contact.isFavourite());
    }

    @Test
    void testContactWebsiteLink() {
        assertEquals("https://johndoe.com", contact.getWebsiteLink());
    }

    @Test
    void testContactLinkedInLink() {
        assertEquals("https://linkedin.com/in/johndoe", contact.getLinkedInLink());
    }

    @Test
    void testContactUser() {
        assertEquals(user, contact.getUser());
        assertEquals("user-123", contact.getUser().getUserId());
    }

    @Test
    void testContactPicture() {
        assertNull(contact.getPicture());
        contact.setPicture("https://example.com/pic.jpg");
        assertEquals("https://example.com/pic.jpg", contact.getPicture());
    }

    @Test
    void testContactBuilder() {
        Contact builtContact = Contact.builder()
                .id("contact-456")
                .name("Jane Doe")
                .email("jane@example.com")
                .phoneNumber("9876543210")
                .address("456 Oak Ave")
                .favourite(true)
                .build();

        assertNotNull(builtContact);
        assertEquals("contact-456", builtContact.getId());
        assertEquals("Jane Doe", builtContact.getName());
        assertTrue(builtContact.isFavourite());
    }
}
