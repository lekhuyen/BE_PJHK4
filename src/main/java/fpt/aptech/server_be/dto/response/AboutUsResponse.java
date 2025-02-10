package fpt.aptech.server_be.dto.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutUsResponse {

    int id;

    String title;

    String description;

    String aboutImage1;

    String aboutImage2;
}
