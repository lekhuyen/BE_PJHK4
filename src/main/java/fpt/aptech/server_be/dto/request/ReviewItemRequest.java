package fpt.aptech.server_be.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewItemRequest {

    int auctionItemId;  // Use Integer instead of int
    String  userId;
    int rating;
    String comment;
    boolean isVerified;
    boolean markAsRated;
}
