package fpt.aptech.server_be.dto.response;



import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRespone {
    Integer category_id;
    String category_name;
    String description;

    public CategoryRespone(CategoryRespone categoryRespone) {
    }
}
