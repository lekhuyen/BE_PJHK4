package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequest {
    int id;
    String message;
    User sender;
}
