package springdev.scm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import springdev.scm.entities.Contact;
import springdev.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

    List<Contact> findByUser(User user);

    @Query("SELECT c FROM contact c WHERE c.user.userId = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM contact c WHERE c.user.userId = :userId AND " +
    "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
List<Contact> findByUserIdAndKeyword(String userId, String keyword);


}
