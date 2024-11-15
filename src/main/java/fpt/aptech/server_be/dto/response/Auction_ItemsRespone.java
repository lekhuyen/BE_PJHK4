package fpt.aptech.server_be.dto.response;

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
public class Auction_ItemsRespone {

    Integer item_id;
    String item_name;
    String description;
    String images;
    Double starting_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    String status;

    Category category;

    public Auction_ItemsRespone(Auction_Items auctionItems) {
    }
}
