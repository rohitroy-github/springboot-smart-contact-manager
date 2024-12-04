package springdev.scm.services;

import java.util.List;

import springdev.scm.entities.Contact;

public interface ContactService {

    Contact save(Contact contact);

    List<Contact> getAll();

    Contact getById(String id);

    void delete(String id);

    List<Contact> searchByName(String name, String email, String phoneNumber);

    List<Contact> getByUserId(String userId);

}
