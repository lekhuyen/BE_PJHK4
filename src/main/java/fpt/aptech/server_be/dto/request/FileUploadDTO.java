package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.Auction_Items;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadDTO {
    int id;
    String fileName;
    String fileType;
    Date createdAt;
    Auction_Items auctionItem;
}
