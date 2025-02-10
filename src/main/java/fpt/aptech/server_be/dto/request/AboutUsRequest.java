package fpt.aptech.server_be.dto.request;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutUsRequest {
    int id;


    String title;


    String description;


    String aboutImage1;


    String aboutImage2;
}
