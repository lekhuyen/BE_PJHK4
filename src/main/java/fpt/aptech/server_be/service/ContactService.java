package fpt.aptech.server_be.service;

import fpt.aptech.server_be.mapper.ContactMapper;
import fpt.aptech.server_be.dto.request.ContactRequest;
import fpt.aptech.server_be.dto.response.ContactRespone;
import fpt.aptech.server_be.entities.Contact;
import fpt.aptech.server_be.repositories.ContactRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactService {

    private final ContactRepository contactRepository;  // Inject ContactRepository instance

    private final ContactMapper contactMapper;  // Inject ContactMapper instance

    // Create a new contact
    public ContactRespone createContact(ContactRequest contactRequest) {
        // Convert ContactRequest to Contact entity
        Contact contact = contactMapper.toEntity(contactRequest);

        // Save the contact entity to the repository using the injected instance
        contact = contactRepository.save(contact);

        // Convert the saved Contact entity back to a ContactRespone DTO
        return contactMapper.toResponse(contact);
    }

    // Update an existing contact (typically to add the admin's reply)
    public ContactRespone updateContact(ContactRespone contactRespone) {
        // Fetch the contact by its ID from the repository
        Optional<Contact> existingContactOpt = contactRepository.findById(contactRespone.getId());

        if (existingContactOpt.isEmpty()) {
            // Return an error or handle it as needed if the contact doesn't exist
            // (You can throw an exception or return null, etc.)
            throw new IllegalArgumentException("Contact not found with ID: " + contactRespone.getId());
        }

        // Get the existing contact entity
        Contact existingContact = existingContactOpt.get();

        // Update fields from the ContactRespone DTO
        existingContact.setReplyMessage(contactRespone.getReplyMessage());  // Set the admin's reply
        existingContact.setInterestedIn(contactRespone.getInterestedIn());  // Update other fields if needed
        // Add other fields to update as necessary (e.g., name, email, etc.)

        // Save the updated contact entity to the repository
        existingContact = contactRepository.save(existingContact);

        // Convert the updated Contact entity back to a ContactRespone DTO
        return contactMapper.toResponse(existingContact);
    }


    // Get all contacts
    public List<ContactRespone> getAllContacts() {
        // Fetch all contacts from the repository
        List<Contact> contacts = contactRepository.findAll();

        // Convert the list of Contact entities to a list of ContactRespone DTOs
        return contacts.stream()
                .map(contactMapper::toResponse)
                .toList();  // Use Java Streams to map all entities to DTOs
    }

    // Get a contact by ID
    public Optional<ContactRespone> getContactById(int id) {
        // Fetch the contact from the repository by ID
        Optional<Contact> contact = contactRepository.findById(id);

        // If found, convert to a ContactRespone DTO, else return empty Optional
        return contact.map(contactMapper::toResponse);
    }
}
