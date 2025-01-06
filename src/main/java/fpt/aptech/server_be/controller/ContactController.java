package fpt.aptech.server_be.controller;

import fpt.aptech.server_be.dto.request.ContactRequest;
import fpt.aptech.server_be.dto.response.ContactRespone;
import fpt.aptech.server_be.service.ContactService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactController {

    private final ContactService contactService;  // Inject ContactService instance

    // Endpoint to create a new contact
    @PostMapping
    public ResponseEntity<ContactRespone> createContact(@RequestBody ContactRequest contactRequest) {
        try {
            ContactRespone contactRespone = contactService.createContact(contactRequest);
            return new ResponseEntity<>(contactRespone, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating contact: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to update an existing contact (e.g., reply to a message)
    @PutMapping("/{id}")
    public ResponseEntity<ContactRespone> updateContact(@PathVariable int id, @RequestBody ContactRespone contactRespone) {
        try {
            contactRespone.setId(id);  // Set the ID of the contact to be updated
            ContactRespone updatedContact = contactService.updateContact(contactRespone);
            return new ResponseEntity<>(updatedContact, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating contact with ID " + id + ": ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all contacts
    @GetMapping
    public ResponseEntity<List<ContactRespone>> getAllContacts() {
        try {
            List<ContactRespone> contacts = contactService.getAllContacts();
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching all contacts: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get a specific contact by ID
    @GetMapping("/{id}")
    public ResponseEntity<ContactRespone> getContactById(@PathVariable int id) {
        try {
            Optional<ContactRespone> contactRespone = contactService.getContactById(id);
            return contactRespone.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching contact with ID " + id + ": ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
