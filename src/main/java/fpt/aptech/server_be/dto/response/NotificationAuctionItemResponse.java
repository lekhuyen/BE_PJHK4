package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.User;
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
public class NotificationAuctionItemResponse {
    int id;
    boolean creatorIsRead = false;
    boolean adminIsRead = false;
    UserResponse creator;
    int auctionItemId;
    Date createdAt = new Date();
    Date updatedAt = new Date();
    String type;
}
