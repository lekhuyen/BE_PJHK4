package fpt.aptech.server_be.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionSuccessEvent {
    private int productId;
    private String sellerId;
    private String buyerEmail;
    private String sellerEmail;
    private String itemName;
    private double price;
}
