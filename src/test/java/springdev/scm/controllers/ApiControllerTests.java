package springdev.scm.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.services.ContactService;

@ExtendWith(MockitoExtension.class)
class ApiControllerTests {

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ApiController apiController;

    private Contact testContact;
    private User testUser;
    private String validApiKey = "131bfbc7-f12b-43e2-b42c-b9c7e1653247";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiController, "API_KEY", validApiKey);

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
    }

    @Test
    void testGetContactById() {
        when(contactService.getById("contact-123")).thenReturn(testContact);

        Contact result = apiController.getContact("contact-123");

        assertNotNull(result);
        assertEquals("contact-123", result.getId());
        assertEquals("John Doe", result.getName());
        verify(contactService, times(2)).getById("contact-123");
    }

    @Test
    void testGetContactByIdNotFound() {
        when(contactService.getById("invalid-id")).thenThrow(new ResourceNotFoundException("Contact not found"));

        assertThrows(ResourceNotFoundException.class, () -> apiController.getContact("invalid-id"));
    }

    @Test
    void testGetUserContactsWithValidApiKey() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(testContact);

        when(contactService.getUserContactsAPI("user-123", validApiKey)).thenReturn(contacts);

        ResponseEntity<?> response = apiController.getUserContacts("user-123", validApiKey);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(contactService, times(1)).getUserContactsAPI("user-123", validApiKey);
    }

    @Test
    void testGetUserContactsWithInvalidApiKey() {
        ResponseEntity<?> response = apiController.getUserContacts("user-123", "invalid-key");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid API key.", response.getBody());
        verify(contactService, times(0)).getUserContactsAPI(anyString(), anyString());
    }

    @Test
    void testGetUserContactsWithEmptyResult() {
        when(contactService.getUserContactsAPI("user-456", validApiKey)).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = apiController.getUserContacts("user-456", validApiKey);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserContactsServiceException() {
        when(contactService.getUserContactsAPI("user-123", validApiKey))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = apiController.getUserContacts("user-123", validApiKey);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
