package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.NotificationAuctionItemResponse;
import fpt.aptech.server_be.entities.NotificationAuctionItem;
import org.mapstruct.Mapper;

@Mapper
public class NotificationAuctionItemMapper {
    public static NotificationAuctionItemResponse toNotificationAuctionItemResponse(NotificationAuctionItem request){
        NotificationAuctionItemResponse response = new NotificationAuctionItemResponse();
        response.setId(request.getId());
        response.setAuctionItemId(request.getAuctionItemId());
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());
        response.setType(request.getType());
        response.setCreator(UserMapper.toUserResponse(request.getCreator()));

        return response;
    }
}
