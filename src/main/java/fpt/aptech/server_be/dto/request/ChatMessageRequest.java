package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {
    int roomId;
    @Nullable
    String content;
    String sender;
    @Nullable
    List<MultipartFile> images = new ArrayList<>();
    @Nullable
    List<String> imagess = new ArrayList<>();
    Date timestamp = new Date();

}
