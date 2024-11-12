package fpt.aptech.server_be.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String name;
    @Size(min = 4, max = 20, message = "INVALID_PASSWORD")
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;
}
