package fpt.aptech.server_be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BiddingResponse {
    int id;
    double price;
    int productId;
    String productName;
    String user;
}
