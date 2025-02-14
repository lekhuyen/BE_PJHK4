package fpt.aptech.server_be.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
//    @Size(min = 4, message = "USERNAME_INVALID")
    String name;
    @Size(min = 4, max = 20, message = "INVALID_PASSWORD")
    String password;
    String email;
    LocalDate dob;
    String phone;
    String address;
    @Builder.Default
    List<String> roles = List.of("USER");
}
