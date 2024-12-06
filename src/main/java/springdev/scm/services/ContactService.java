package springdev.scm.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import springdev.scm.entities.Contact;
import springdev.scm.forms.ContactForm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {

    Contact save(ContactForm contactForm, Authentication authentication);

    List<Contact> getAll();

    Contact getById(String id);

    void delete(String id);

    List<Contact> searchByName(String name, String email, String phoneNumber);

    Page<Contact> getByUserId(String userId, Pageable pageable);

    Page<Contact> searchByUserAndKeyword(String userId, String keyword, Pageable pageable);

}
