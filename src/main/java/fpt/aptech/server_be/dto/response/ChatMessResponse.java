package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessResponse {
    int id;
    String content;
    int chatRoomId;
    String senderId;
    Date timestamp;
}
