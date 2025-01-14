package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessResponse {
    int id;
    String content;
    int roomId;
    String senderId;
    Date timestamp;
    @Nullable
    List<String> images;
}
