package fpt.aptech.server_be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewItemResponse {

    int id;
    int rating;
    boolean markAsRated;
    String comment;
    Date createdAt;
    boolean isVerified;
    String userName;
    String auctionItemName;


}
