package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.User;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String name;
//    String firstName;
//    String lastName;
    String email;
    LocalDate dob;
    String ciNumber;
    String address;
    boolean isActive;
    Set<RoleResponse> roles;

    public UserResponse(User userUpdated) {
    }
}
