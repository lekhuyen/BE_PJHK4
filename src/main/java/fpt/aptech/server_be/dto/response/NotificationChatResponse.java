package fpt.aptech.server_be.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationChatResponse {
    int notiId;
    int chatroomId;
    boolean isRead;

    int quantityBuyer;
    String buyerId;
    int quantitySeller;
    String sellerId;
}
