package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BiddingRequest {
    double price;
     int productId;
    String userId;
    String seller;
}
