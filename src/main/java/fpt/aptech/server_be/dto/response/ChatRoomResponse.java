package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.Auction_Items;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRoomResponse {
    int roomId;
    String userId;
    String buyerName;
    String sellerName;
    int item_id;
    String item_name;
    Double starting_price;
    Double current_price;
    List<String> images;
    ChatMessResponse message;


    NotificationChatResponse notification;
}
