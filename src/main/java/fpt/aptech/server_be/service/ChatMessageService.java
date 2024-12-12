package fpt.aptech.server_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.aptech.server_be.dto.response.ChatMessResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.mapper.ChatMessageMapper;
import fpt.aptech.server_be.repositories.ChatMessageRepository;

import fpt.aptech.server_be.repositories.ChatRoomRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ChatMessageRepository chatMessageRepository;
    UserRepository userRepository;
    ChatRoomRepository chatRoomRepository;

//    public List<ChatMessage> getMessages(ChatRoom chatRoom) {
//        return chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
//    }
    public List<ChatMessResponse> getMessages(int chatRoom) {
        ChatRoom chatRoom1 = chatRoomRepository.findById(chatRoom).get();
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom1);

        return chatMessages.stream().map(ChatMessageMapper::toChatMessageResponse).collect(Collectors.toList());
    }

    public String sendMessage(ChatRoom chatRoom, String sender, String content) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());

        User user = new User();
        user.setId(sender);

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(user);
        message.setContent(content);
        message.setTimestamp(new Date());


        chatMessageRepository.save(message);

        ChatMessResponse chatMessResponse = new ChatMessResponse();
        chatMessResponse.setId(message.getId());
        chatMessResponse.setChatRoomId(message.getChatRoom().getId());
        chatMessResponse.setSenderId(message.getSender().getId());
        chatMessResponse.setTimestamp(message.getTimestamp());
        chatMessResponse.setContent(message.getContent());

        return om.writeValueAsString(chatMessResponse);
    }

    public List<ChatMessResponse> getAllMessagesByBuyerId(int chatRoomId,String buyerId) {
        User user = userRepository.findById(buyerId).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomAndSender(chatRoom, user);
        return chatMessages.stream()
                .map(ChatMessageMapper::toChatMessageResponse)
                .collect(Collectors.toList());
    }
}
