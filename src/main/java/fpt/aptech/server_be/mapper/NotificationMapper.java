package fpt.aptech.server_be.mapper;
import fpt.aptech.server_be.dto.response.NotificationResponse;
import fpt.aptech.server_be.entities.Notification;
import org.mapstruct.Mapper;

@Mapper
public class NotificationMapper {
    public static NotificationResponse toNotificationResponse(Notification request) {

        if (request == null) {
            return null;
        }
        return NotificationResponse.builder()
                .id(request.getId())
                .buyerIsRead(request.isBuyerIsRead())
                .sellerIsRead(request.isSellerIsRead())
                .price(request.getPrice())
                .productId(request.getBidding().getAuction_Items().getItem_id())
                .productName(request.getBidding().getAuction_Items().getItem_name())
                .buyerId(request.getBuyer().getId())
                .buyerName(request.getBuyer().getName())
                .sellerId(request.getSeller().getId())
                .sellerName(request.getSeller().getName())
                .timestamp(request.getDate())
                .build();
    }
}
