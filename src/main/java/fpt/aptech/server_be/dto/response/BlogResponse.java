package fpt.aptech.server_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogResponse {
    int id;
    String title;
    String  author;
    String content;
    Date blogDate;
    String blogImage;
}
