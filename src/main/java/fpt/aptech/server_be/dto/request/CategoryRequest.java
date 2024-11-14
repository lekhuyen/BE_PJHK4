package fpt.aptech.server_be.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    Integer category_id;
    String category_name;
    String description;

}
