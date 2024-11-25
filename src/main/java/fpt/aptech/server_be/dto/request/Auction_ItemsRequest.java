package fpt.aptech.server_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Auction_ItemsRequest {
    int item_id;
    String userId;
    String item_name;
    String description;
    String images;
    Double starting_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    String status;
    int category_id;
}

