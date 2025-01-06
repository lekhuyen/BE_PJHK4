package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.enums.CountryCode;
import fpt.aptech.server_be.enums.InterestedIn;
import lombok.*;
import lombok.experimental.FieldDefaults;


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

    InterestedIn interestedIn;

    String message;

    String replyMessage;
}
