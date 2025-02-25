package fpt.aptech.server_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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
    List<String> images;
    Double starting_price;
    Double current_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    boolean status;
    boolean isSell;
    boolean isSoldout;
    boolean isPaid;

    CategoryResponse category;
    UserResponse user;
    UserResponse buyer;

    BiddingResponse bidding;

    public Auction_ItemsResponse(int itemId, String itemName, String description, List<String> images, Double startingPrice, LocalDate startDate, LocalDate endDate, String bidStep) {
    }
}
