package springdev.scm.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.forms.ContactForm;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.implementations.ContactServiceImpl;
import springdev.scm.repositories.ContactRepo;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTests {

    @Mock
    private ContactRepo contactRepo;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact testContact;
    private User testUser;
    private ContactForm contactForm;

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn("test@example.com");

        testUser = new User();
        testUser.setUserId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testContact = new Contact();
        testContact.setId("contact-123");
        testContact.setName("John Doe");
        testContact.setEmail("john@example.com");
        testContact.setPhoneNumber("1234567890");
        testContact.setUser(testUser);

        contactForm = new ContactForm();
        contactForm.setName("John Doe");
        contactForm.setEmail("john@example.com");
        contactForm.setPhoneNumber("1234567890");
        contactForm.setAddress("123 Main St");
        contactForm.setDescription("Friend");
    }

    @Test
    void testSaveContact() {
        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactRepo.saveAndFlush(any(Contact.class))).thenReturn(testContact);

        Contact savedContact = contactService.save(contactForm, authentication);

        assertNotNull(savedContact);
        assertEquals("John Doe", savedContact.getName());
        assertEquals("john@example.com", savedContact.getEmail());
        verify(contactRepo, times(1)).saveAndFlush(any(Contact.class));
    }

    @Test
    void testGetContactById() {
        when(contactRepo.findById("contact-123")).thenReturn(Optional.of(testContact));

        Contact foundContact = contactService.getById("contact-123");

        assertNotNull(foundContact);
        assertEquals("John Doe", foundContact.getName());
    }

    @Test
    void testGetContactByIdNotFound() {
        when(contactRepo.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactService.getById("invalid-id"));
    }

    @Test
    void testUpdateContact() {
        ContactForm updateForm = new ContactForm();
        updateForm.setId("contact-123");
        updateForm.setName("Jane Doe");
        updateForm.setEmail("jane@example.com");

        when(contactRepo.findById("contact-123")).thenReturn(Optional.of(testContact));
        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactRepo.saveAndFlush(any(Contact.class))).thenReturn(testContact);

        Contact updatedContact = contactService.update(updateForm, authentication);

        assertNotNull(updatedContact);
        verify(contactRepo, times(1)).saveAndFlush(any(Contact.class));
    }

    @Test
    void testDeleteContact() {
        testContact.getUser().getContacts().add(testContact);
        when(contactRepo.findById("contact-123")).thenReturn(Optional.of(testContact));
        doNothing().when(contactRepo).deleteById(anyString());
        doNothing().when(contactRepo).flush();

        assertDoesNotThrow(() -> contactService.delete("contact-123"));

        verify(contactRepo, times(1)).deleteById("contact-123");
    }

    @Test
    void testDeleteContactNotFound() {
        when(contactRepo.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactService.delete("invalid-id"));
    }

    @Test
    void testGetByUserId() {
        Page<Contact> contactPage = new PageImpl<>(java.util.List.of(testContact));
        when(contactRepo.findByUserId("user-123", PageRequest.of(0, 8))).thenReturn(contactPage);

        Page<Contact> result = contactService.getByUserId("user-123", PageRequest.of(0, 8));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(contactRepo, times(1)).findByUserId("user-123", PageRequest.of(0, 8));
    }

    @Test
    void testSearchByUserAndKeyword() {
        Page<Contact> searchResult = new PageImpl<>(java.util.List.of(testContact));
        when(contactRepo.findByUserIdAndKeyword("user-123", "john", PageRequest.of(0, 8)))
                .thenReturn(searchResult);

        Page<Contact> result = contactService.searchByUserAndKeyword("user-123", "john", PageRequest.of(0, 8));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

}
