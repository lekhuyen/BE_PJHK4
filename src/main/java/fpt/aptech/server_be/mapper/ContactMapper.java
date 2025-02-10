package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.request.ContactRequest;
import fpt.aptech.server_be.dto.response.ContactRespone;
import fpt.aptech.server_be.entities.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    // Mapping from ContactRequest (DTO) to Contact (Entity)
    public Contact toEntity(ContactRequest contactRequest) {
        if (contactRequest == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setPhone(contactRequest.getPhone());
        contact.setCountryCode(contactRequest.getCountryCode());
        contact.setInterestedIn(contactRequest.getInterestedIn());
        contact.setMessage(contactRequest.getMessage());
        contact.setReplyMessage(contactRequest.getReplyMessage() != null ? contactRequest.getReplyMessage() : "");

        // Set countryPhoneCode based on the countryCode
        if (contactRequest.getCountryCode() != null) {
            contact.setCountryPhoneCode(contactRequest.getCountryCode().getCode());
        }

        return contact;
    }

    // Mapping from Contact (Entity) to ContactRespone (DTO)
    public ContactRespone toResponse(Contact contact) {
        if (contact == null) {
            return null;
        }

        ContactRespone contactResponse = new ContactRespone();
        contactResponse.setId(contact.getId());
        contactResponse.setName(contact.getName());
        contactResponse.setEmail(contact.getEmail());
        contactResponse.setPhone(contact.getPhone());
        contactResponse.setCountryCode(contact.getCountryCode());
        contactResponse.setInterestedIn(contact.getInterestedIn());
        contactResponse.setMessage(contact.getMessage());
        contactResponse.setReplyMessage(contact.getReplyMessage());
        contactResponse.setReceivetime(contact.getReceivetime());  // Ensure receivetime is mapped
        contactResponse.setReplyTime(contact.getReplyTime());

        // Set formattedCountryCode here
        contactResponse.setFormattedCountryCode(contact.getFormattedCountryCode());

        return contactResponse;
    }


    // Mapping from ContactRespone (DTO) to Contact (Entity) - Typically used for updating entities
    public Contact toEntityFromResponse(ContactRespone contactResponse) {
        if (contactResponse == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setId(contactResponse.getId());
        contact.setName(contactResponse.getName());
        contact.setEmail(contactResponse.getEmail());
        contact.setPhone(contactResponse.getPhone());
        contact.setCountryCode(contactResponse.getCountryCode());
        contact.setInterestedIn(contactResponse.getInterestedIn());
        contact.setMessage(contactResponse.getMessage());
        contact.setReplyMessage(contactResponse.getReplyMessage() != null ? contactResponse.getReplyMessage() : "");

        // Set countryPhoneCode
        contact.setCountryPhoneCode(contactResponse.getFormattedCountryCode());

        return contact;
    }
}
