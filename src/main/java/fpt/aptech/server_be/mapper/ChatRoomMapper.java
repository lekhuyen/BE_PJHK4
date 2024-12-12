package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.dto.response.UserResponse;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import org.mapstruct.Mapper;

@Mapper
public class ChatRoomMapper {
    public static ChatRoomResponse toChatRoomResponse(ChatRoom request){
        ChatRoomResponse response = new ChatRoomResponse();
        response.setRoomId(request.getId());
        response.setUserId(request.getSeller().getId());
        response.setBuyerName(request.getBuyer().getName());
        response.setSellerName(request.getSeller().getName());
        response.setItem_id(request.getAcAuctionItem().getItem_id());
        response.setItem_name(request.getAcAuctionItem().getItem_name());
        response.setStarting_price(request.getAcAuctionItem().getStarting_price());
        response.setCurrent_price(request.getAcAuctionItem().getCurrent_price());
        response.setImages(request.getAcAuctionItem().getImages());

        return response;
    }
}
