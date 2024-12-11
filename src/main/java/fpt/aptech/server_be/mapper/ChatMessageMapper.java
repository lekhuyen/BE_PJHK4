package fpt.aptech.server_be.mapper;

import fpt.aptech.server_be.dto.response.ChatMessResponse;
import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import org.mapstruct.Mapper;

@Mapper
public class ChatMessageMapper {
    public static ChatMessResponse toChatMessageResponse(ChatMessage request){
        return new ChatMessResponse(
                request.getId(),
                request.getContent(),
                request.getChatRoom().getId(),
                request.getSender().getId(),
                request.getTimestamp()
        );
    }
}
