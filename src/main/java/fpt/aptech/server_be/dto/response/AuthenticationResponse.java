package fpt.aptech.server_be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;
    private String username; // ✅ Thêm trường username
    private String userId;
    private String phone;
    private String address;
}
