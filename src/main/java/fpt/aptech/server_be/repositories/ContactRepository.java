package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

}
