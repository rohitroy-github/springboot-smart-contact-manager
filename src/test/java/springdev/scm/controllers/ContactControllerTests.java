package springdev.scm.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;

@ExtendWith(MockitoExtension.class)
class ContactControllerTests {

    @Mock
    private ContactService contactService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ContactController contactController;

    private Contact testContact;
    private User testUser;

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn("test@example.com");

        testUser = new User();
        testUser.setUserId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setContacts(new ArrayList<>());

        testContact = new Contact();
        testContact.setId("contact-123");
        testContact.setName("John Doe");
        testContact.setEmail("john@example.com");
        testContact.setPhoneNumber("1234567890");
        testContact.setUser(testUser);
    }

    @Test
    void testViewAddContactPage() throws Exception {
        Model model = new ExtendedModelMap();

        String view = contactController.addContactView(model);

        assertEquals("user/add_contact", view);
        assertNotNull(model.getAttribute("contactFormData"));
        assertEquals("add_new_contact", model.getAttribute("process_type"));
    }

    @Test
    void testViewAllContacts() {
        Model model = new ExtendedModelMap();
        Page<Contact> contactPage = new PageImpl<>(java.util.List.of(testContact));

        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactService.getByUserId("user-123", PageRequest.of(0, 8))).thenReturn(contactPage);

        String view = contactController.viewContacts(0, 8, null, model, authentication, session);

        assertEquals("user/all_contacts", view);
        assertNotNull(model.getAttribute("contacts"));
        assertEquals(0, model.getAttribute("currentPage"));
    }

    @Test
    void testViewAllContactsWithSearch() {
        Model model = new ExtendedModelMap();
        Page<Contact> searchResult = new PageImpl<>(java.util.List.of(testContact));

        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactService.searchByUserAndKeyword("user-123", "john", PageRequest.of(0, 8)))
                .thenReturn(searchResult);

        String view = contactController.viewContacts(0, 8, "john", model, authentication, session);

        assertEquals("user/all_contacts", view);
        assertNotNull(model.getAttribute("contacts"));
        assertEquals("john", model.getAttribute("search"));
    }

    @Test
    void testDeleteContact() {
        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactService.getById("contact-123")).thenReturn(testContact);
        doNothing().when(contactService).delete("contact-123");

        String view = contactController.deleteContact("contact-123", authentication, session);

        assertEquals("redirect:/user/contact/all-contacts", view);
        verify(contactService, times(1)).delete("contact-123");
    }

    @Test
    void testDeleteContactNotFound() {
        when(userService.getUserByEmail(anyString())).thenReturn(testUser);
        when(contactService.getById("invalid-id")).thenThrow(new ResourceNotFoundException("Contact not found"));

        String view = contactController.deleteContact("invalid-id", authentication, session);

        assertEquals("redirect:/user/contact/all-contacts", view);
    }

    @Test
    void testEditContactView() {
        Model model = new ExtendedModelMap();
        when(contactService.getById("contact-123")).thenReturn(testContact);

        String view = contactController.editContactView("contact-123", model, authentication);

        assertEquals("user/add_contact", view);
        assertNotNull(model.getAttribute("contactFormData"));
        assertEquals("update_contact", model.getAttribute("process_type"));
        assertEquals("contact-123", model.getAttribute("contactId_to_be_updated"));
    }

}
