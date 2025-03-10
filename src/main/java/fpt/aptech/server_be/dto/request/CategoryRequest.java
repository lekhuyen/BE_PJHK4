package fpt.aptech.server_be.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    int category_id;
    String category_name;
    String description;

    public CategoryRequest(String category_name) {
        this.category_name = category_name;
    }
}
