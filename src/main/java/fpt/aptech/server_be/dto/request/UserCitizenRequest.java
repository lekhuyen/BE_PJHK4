package fpt.aptech.server_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCitizenRequest {
    int id;
    String ciCode;
    String fullName;
    String address;
    String birthDate;
    String startDate;
    String userId;
}
