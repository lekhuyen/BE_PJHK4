package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.User;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String name;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;

    public UserResponse(User userUpdated) {
    }
}
