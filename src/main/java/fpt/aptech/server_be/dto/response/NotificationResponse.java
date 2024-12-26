package fpt.aptech.server_be.dto.response;

import fpt.aptech.server_be.entities.Bidding;
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
public class NotificationResponse {
    int id;
    boolean sellerIsRead = false;
    boolean buyerIsRead = false;
    String message;
    double price;
    int productId;
    String productName;

    String sellerId;
    String sellerName;

    String buyerId;
    String buyerName;

    Date timestamp;
}
