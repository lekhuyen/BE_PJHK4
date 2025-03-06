package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.ChatMessResponse;
import fpt.aptech.server_be.dto.response.ChatMessageResponse;
import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.dto.response.NotificationChatResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public class ChatRoomMapper {
    public static ChatRoomResponse toChatRoomResponse(ChatRoom request){
        ChatRoomResponse response = new ChatRoomResponse();
        response.setRoomId(request.getId());
        response.setUserId(request.getSeller().getId());
        response.setSellerId(request.getSeller().getId());
        response.setBuyerName(request.getBuyer().getName());
        response.setSellerName(request.getSeller().getName());
        response.setItem_id(request.getAcAuctionItem().getItem_id());
        response.setItem_name(request.getAcAuctionItem().getItem_name());
        response.setStarting_price(request.getAcAuctionItem().getStarting_price());
        response.setCurrent_price(request.getAcAuctionItem().getCurrent_price());
        response.setImages(request.getAcAuctionItem().getImages());


        if (request.getMessage() != null) {
            List<ChatMessResponse> messages = request.getMessage()
                    .stream()
                    .map(ChatMessageMapper::toChatMessageResponse)
                    .toList();
            response.setListMessages(messages);
        }


        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            ChatMessage message = request.getMessage()
                    .stream()
                    .reduce((first, second) -> second)
                    .orElse(null);
            response.setMessage(ChatMessageMapper.toChatMessageResponse(message));
        }

        NotificationChatResponse notificationChatResponse = new NotificationChatResponse();
        if (request.getNotificationChat() != null) {
            notificationChatResponse.setNotiId(request.getNotificationChat().getId());
            notificationChatResponse.setRead(request.getNotificationChat().isRead());


            notificationChatResponse.setChatroomId(request.getId());


            notificationChatResponse.setQuantitySeller(request.getNotificationChat().getQuantitySeller());
            notificationChatResponse.setQuantityBuyer(request.getNotificationChat().getQuantityBuyer());
            notificationChatResponse.setBuyerId(request.getNotificationChat().getBuyerId());
            notificationChatResponse.setSellerId(request.getNotificationChat().getSellerId());
        }


        response.setNotification(notificationChatResponse);

        return response;
    }
}
