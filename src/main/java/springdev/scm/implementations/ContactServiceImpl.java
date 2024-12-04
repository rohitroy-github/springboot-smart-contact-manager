package springdev.scm.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springdev.scm.entities.Contact;
import springdev.scm.helper.ResourceNotFoundException;
import springdev.scm.repositories.ContactRepo;
import springdev.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact save(Contact contact) {

        String generatedUserId = UUID.randomUUID().toString();
        contact.setId(generatedUserId);

        return contactRepo.save(contact);

    }

    @Override
    public List<Contact> getAll() {

        return contactRepo.findAll();

    }

    @Override
    public Contact getById(String id) {

        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID:" + id));

    }

    @Override
    public void delete(String id) {

        Contact contactToBeDeleted = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't fetch contact with ID:" + id));
        contactRepo.delete(contactToBeDeleted);

    }
    
    // @Override
    // public List<Contact> search(String name, String email, String phoneNumber) {


    // }


    @Override
    public List<Contact> getByUserId(String userId) {

        return contactRepo.findByUserId(userId);

     
    }

    @Override
    public List<Contact> searchByName(String name, String email, String phoneNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByName'");
    }

}
