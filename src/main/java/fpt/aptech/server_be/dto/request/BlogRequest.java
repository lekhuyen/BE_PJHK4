package fpt.aptech.server_be.dto.request;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogRequest {

    String title;
    String author;
    String content;
    String blogImage;
}
