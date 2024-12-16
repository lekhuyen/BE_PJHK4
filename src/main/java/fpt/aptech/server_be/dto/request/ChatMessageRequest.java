package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {
    int roomId;
    String content;
    String sender;
    List<String> images = new ArrayList<>();
}
