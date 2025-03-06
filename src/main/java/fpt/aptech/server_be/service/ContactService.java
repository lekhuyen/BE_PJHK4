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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactService {

    private final ContactRepository contactRepository;  // Inject ContactRepository instance
    private final ContactMapper contactMapper;  // Inject ContactMapper instance
    private final JavaMailSender emailSender;  // Inject JavaMailSender to send emails

    // Create a new contact
    public ContactRespone createContact(ContactRequest contactRequest) {
        try {
            Contact contact = contactMapper.toEntity(contactRequest);
            contact = contactRepository.save(contact);
            return contactMapper.toResponse(contact);
        } catch (Exception e) {
            log.error("Error creating contact", e);
            throw new RuntimeException("Error creating contact: " + e.getMessage());
        }
    }

    // Update an existing contact (typically to add the admin's reply)
    public ContactRespone updateContact(ContactRespone contactRespone) {
        // Fetch the contact by its ID from the repository
        Optional<Contact> existingContactOpt = contactRepository.findById(contactRespone.getId());

        if (existingContactOpt.isEmpty()) {
            // Handle the case if the contact doesn't exist
            throw new IllegalArgumentException("Contact not found with ID: " + contactRespone.getId());
        }

        // Get the existing contact entity
        Contact existingContact = existingContactOpt.get();

        // Update fields from the ContactRespone DTO
        existingContact.setReplyMessage(contactRespone.getReplyMessage());  // Set the admin's reply
        existingContact.setInterestedIn(contactRespone.getInterestedIn());  // Update other fields if needed
        existingContact.setReplyTime(LocalDateTime.now());  // Set the reply time when updating the contact

        // Save the updated contact entity to the repository
        existingContact = contactRepository.save(existingContact);

        // Convert the updated Contact entity back to a ContactRespone DTO
        ContactRespone updatedContact = contactMapper.toResponse(existingContact);

        // Send an email to the user
        sendReplyEmail(existingContact);

        return updatedContact;
    }

    // Send an email to the user after the reply message is updated
    private static final String FROM_EMAIL = "biddora38@gmail.com";  // Set your website's contact email
    private static final String COMPANY_NAME = "Biddora";
    private static final String WEBSITE_URL = "https://biddora.com";
    private static final String SUPPORT_EMAIL = "biddora38@gmail.com";

    public void sendReplyEmail(Contact contact) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);  // Set email to appear from website support
            message.setTo(contact.getEmail());  // Send to the recipient
            message.setSubject("Thank You for Contacting Us - " + COMPANY_NAME);

            String emailContent = "Dear " + contact.getName() + ",\n\n"
                    + "Thank you for reaching out to us at " + COMPANY_NAME + ". We have received your inquiry and are happy to assist you.\n\n"
                    + "Here is our response to your inquiry:\n"
                    + "-------------------------------------------\n"
                    + contact.getReplyMessage() + "\n"
                    + "-------------------------------------------\n\n"
                    + "If you have any further questions, feel free to contact us again.\n\n"
                    + "Best Regards,\n"
                    + COMPANY_NAME + " Support Team\n"
                    + "Website: " + WEBSITE_URL + "\n"
                    + "Support Email: " + SUPPORT_EMAIL + "\n\n"
                    + "This is an automated message. Please do not reply to this email.";

            message.setText(emailContent);

            // Send the email
            emailSender.send(message);
            log.info("Reply email sent to: " + contact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send reply email to: " + contact.getEmail(), e);
        }
    }

    // Get all contacts
    public List<ContactRespone> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        return contacts.stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    // Get a contact by ID
    public Optional<ContactRespone> getContactById(int id) {
        Optional<Contact> contact = contactRepository.findById(id);
        return contact.map(contactMapper::toResponse);
    }
}
