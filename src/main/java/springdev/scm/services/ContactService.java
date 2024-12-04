package springdev.scm.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import springdev.scm.entities.Contact;
import springdev.scm.forms.ContactForm;

public interface ContactService {

    Contact save(ContactForm contactForm, Authentication authentication);

    List<Contact> getAll();

    Contact getById(String id);

    void delete(String id);

    List<Contact> searchByName(String name, String email, String phoneNumber);

    List<Contact> getByUserId(String userId);

}
