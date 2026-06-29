package springdev.scm.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import springdev.scm.entities.Providers;
import springdev.scm.entities.User;

@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("user-123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("encrypted-password");
        testUser.setEnabled(true);
        testUser.setProvider(Providers.SELF);
        testUser = userRepo.save(testUser);
    }

    @Test
    void testSaveUser() {
        assertNotNull(testUser);
        assertEquals("user-123", testUser.getUserId());
        assertEquals("test@example.com", testUser.getEmail());
    }

    @Test
    void testFindUserById() {
        Optional<User> foundUser = userRepo.findById("user-123");

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    void testFindUserByIdNotFound() {
        Optional<User> foundUser = userRepo.findById("invalid-id");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindUserByEmail() {
        Optional<User> foundUser = userRepo.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    void testFindUserByEmailNotFound() {
        Optional<User> foundUser = userRepo.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        testUser.setName("Updated User");
        testUser.setEnabled(false);
        User updatedUser = userRepo.save(testUser);

        assertEquals("Updated User", updatedUser.getName());
        assertFalse(updatedUser.isEnabled());
    }

    @Test
    void testDeleteUser() {
        userRepo.deleteById("user-123");

        Optional<User> deletedUser = userRepo.findById("user-123");
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUserEmailIsUnique() {
        User duplicateUser = new User();
        duplicateUser.setUserId("user-456");
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setName("Duplicate User");
        duplicateUser.setPassword("password");

        // This should violate unique constraint
        assertThrows(Exception.class, () -> userRepo.saveAndFlush(duplicateUser));
    }

    @Test
    void testFindAllUsers() {
        long userCount = userRepo.count();

        assertTrue(userCount > 0);
        assertTrue(userCount >= 1);
    }

}
