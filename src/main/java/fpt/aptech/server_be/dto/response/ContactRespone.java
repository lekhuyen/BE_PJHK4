package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.enums.CountryCode;
import fpt.aptech.server_be.enums.InterestedIn;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactRespone {
    int id;
    String name;
    String email;
    String phone;
    CountryCode countryCode;
    String formattedCountryCode;  // Optionally add formatted country code here
    InterestedIn interestedIn;
    String message;
    String replyMessage;
    LocalDateTime receivetime;  // Add receivetime here
    LocalDateTime replyTime;
}
