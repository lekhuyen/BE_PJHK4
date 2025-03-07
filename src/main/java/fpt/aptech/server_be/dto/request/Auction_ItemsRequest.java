package fpt.aptech.server_be.dto.request;

import fpt.aptech.server_be.entities.FileUploadFDF;
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
public class Auction_ItemsRequest {
    int item_id;
    String item_name;
    String description;
    List<MultipartFile> images;
    Double starting_price;
    LocalDate start_date;
    LocalDate end_date;
    String bid_step;
    boolean status;
    boolean isSell;
    boolean isSoldout;
//    double width;
//    double height;

    String userId;
    int category_id;

    List<MultipartFile> fileUploads; // ✅ FIXED: Accepts file uploads from frontend
}

