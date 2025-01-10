package fpt.aptech.server_be.dto.response;

import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String content;
    int roomId;
    String senderId;
    Date timestamp;
    @Nullable
    List<String> images;
}
