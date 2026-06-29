package springdev.scm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import springdev.scm.controllers.ContactController;
import springdev.scm.controllers.ApiController;
import springdev.scm.services.ContactService;
import springdev.scm.services.UserService;
import springdev.scm.repositories.ContactRepo;
import springdev.scm.repositories.UserRepo;

@SpringBootTest
class ScmApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired(required = false)
	private ContactController contactController;

	@Autowired(required = false)
	private ApiController apiController;

	@Autowired(required = false)
	private ContactService contactService;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private ContactRepo contactRepo;

	@Autowired(required = false)
	private UserRepo userRepo;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should be loaded");
	}

	@Test
	void testControllersAreWired() {
		assertNotNull(contactController, "ContactController should be wired");
		assertNotNull(apiController, "ApiController should be wired");
	}

	@Test
	void testServicesAreWired() {
		assertNotNull(contactService, "ContactService should be wired");
		assertNotNull(userService, "UserService should be wired");
	}

	@Test
	void testRepositoriesAreWired() {
		assertNotNull(contactRepo, "ContactRepo should be wired");
		assertNotNull(userRepo, "UserRepo should be wired");
	}

	@Test
	void testApplicationStartup() {
		String appName = applicationContext.getEnvironment().getProperty("spring.application.name");
		assertEquals("scm", appName, "Application name should be 'scm'");
	}

}

