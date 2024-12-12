package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;

@Mapper
public class ChatRoomMapper {
    public static ChatRoomResponse toChatRoomResponse(ChatRoom request){
        return new ChatRoomResponse(
                request.getId(),
                request.getSeller().getId(),
                request.getSeller().getName(),
                request.getBuyer().getName(),
                request.getAcAuctionItem().getItem_id(),
                request.getAcAuctionItem().getItem_name(),
                request.getAcAuctionItem().getStarting_price(),
                request.getAcAuctionItem().getCurrent_price(),
                request.getAcAuctionItem().getImages()
        );
    }
}
