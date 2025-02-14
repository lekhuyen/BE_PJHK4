package fpt.aptech.server_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionSuccessDTO {
    private int productId;
    private String sellerId;
    private String correlationId;
}
