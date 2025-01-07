package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

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


        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            ChatMessage message = request.getMessage()
                    .stream()
                    .reduce((first, second) -> second)
                    .orElse(null);
            if (message != null) {
                response.setMessage(ChatMessageMapper.toChatMessageResponse(message));
            }
        }
        return response;
    }
}
