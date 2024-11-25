package fpt.aptech.server_be.dto.response;



import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Integer category_id;
    String category_name;
    String description;
    List<Auction_ItemsResponse> auction_items;

    public CategoryResponse(CategoryResponse categoryRespone) {
    }
}
