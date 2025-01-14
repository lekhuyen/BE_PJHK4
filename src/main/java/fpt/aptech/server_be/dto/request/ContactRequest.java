package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.enums.CountryCode;
import fpt.aptech.server_be.enums.InterestedIn;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactRequest {

    String name;
    String email;
    String phone;
    CountryCode countryCode;
    InterestedIn interestedIn;  // Enum field
    String message;
    String replyMessage;
}

