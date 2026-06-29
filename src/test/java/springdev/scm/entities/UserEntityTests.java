package springdev.scm.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityTests {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user-123");
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("encrypted-password");
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setProvider(Providers.SELF);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals("user-123", user.getUserId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
    }

    @Test
    void testUserEmail() {
        assertEquals("test@example.com", user.getEmail());
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    void testUserPassword() {
        assertEquals("encrypted-password", user.getPassword());
    }

    @Test
    void testUserEnabled() {
        assertTrue(user.isEnabled());
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testUserEmailVerified() {
        assertFalse(user.isEmailVerified());
        user.setEmailVerified(true);
        assertTrue(user.isEmailVerified());
    }

    @Test
    void testUserProvider() {
        assertEquals(Providers.SELF, user.getProvider());
        user.setProvider(Providers.GOOGLE);
        assertEquals(Providers.GOOGLE, user.getProvider());
    }

    @Test
    void testUserContactsList() {
        user.setContacts(new ArrayList<>());
        assertEquals(0, user.getContacts().size());

        Contact contact = new Contact();
        contact.setId("contact-123");
        contact.setName("John Doe");
        user.getContacts().add(contact);

        assertEquals(1, user.getContacts().size());
    }

    @Test
    void testUserRoles() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoleList(roles);

        assertEquals(1, user.getRoleList().size());
        assertTrue(user.getRoleList().contains("ROLE_USER"));
    }

    @Test
    void testUserGetUsername() {
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void testUserIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testUserIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testUserIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testUserProfilePicture() {
        assertNull(user.getProfilePicture());
        user.setProfilePicture("https://example.com/profile.jpg");
        assertEquals("https://example.com/profile.jpg", user.getProfilePicture());
    }

    @Test
    void testUserPhoneNumber() {
        assertNull(user.getPhoneNumber());
        user.setPhoneNumber("1234567890");
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    void testUserAbout() {
        assertNull(user.getAbout());
        user.setAbout("This is my bio");
        assertEquals("This is my bio", user.getAbout());
    }

    @Test
    void testUserBuilder() {
        User builtUser = User.builder()
                .userId("user-456")
                .email("john@example.com")
                .name("John Smith")
                .password("password123")
                .enabled(true)
                .emailVerified(true)
                .provider(Providers.GOOGLE)
                .build();

        assertNotNull(builtUser);
        assertEquals("user-456", builtUser.getUserId());
        assertEquals("john@example.com", builtUser.getEmail());
        assertTrue(builtUser.isEmailVerified());
    }
}
