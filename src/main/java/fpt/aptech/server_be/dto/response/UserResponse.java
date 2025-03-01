package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.User;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
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
    String email;
    LocalDate dob;
    String ciNumber;
    String address;
    String phone;
    Boolean isVerify;

    boolean isActive;
    Set<RoleResponse> roles;

    private List<String> addresses;

}
