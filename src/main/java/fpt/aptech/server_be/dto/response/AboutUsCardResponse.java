package fpt.aptech.server_be.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutUsCardResponse {

    int id;
    String title;
    String description;
    String aboutCardImage;
}
