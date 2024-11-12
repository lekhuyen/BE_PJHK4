package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.Permission;
import fpt.aptech.server_be.entities.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String name;
    String description;
    Set<PermissionResponse> permissions;

    public RoleResponse(Role role) {
    }
}
