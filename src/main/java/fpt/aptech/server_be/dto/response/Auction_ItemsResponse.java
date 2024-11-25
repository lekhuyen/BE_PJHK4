package fpt.aptech.server_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auction_ItemsResponse {

    int item_id;
    String item_name;
    String description;
    String images;
    Double starting_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    String status;

    CategoryResponse category;
    UserResponse user;

    public Auction_ItemsResponse(Auction_Items auctionItems) {
    }

    public Auction_ItemsResponse(int itemId, String itemName, String description, String images, Double startingPrice, LocalDate startDate, LocalDate endDate, String bidStep, String status) {
    }
}
