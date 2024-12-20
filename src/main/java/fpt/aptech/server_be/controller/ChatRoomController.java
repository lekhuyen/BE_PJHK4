package fpt.aptech.server_be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpt.aptech.server_be.dto.request.ChatMessageRequest;
import fpt.aptech.server_be.dto.response.CategoryResponse;
import fpt.aptech.server_be.dto.response.ChatMessResponse;
import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.repositories.ChatRoomRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import fpt.aptech.server_be.service.ChatMessageService;
import fpt.aptech.server_be.service.ChatRoomService;
import fpt.aptech.server_be.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.Mutable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatRoomController {
    ChatRoomService chatRoomService;
    ChatMessageService chatMessageService;
    UserRepository userRepository;
    ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/room/{productId}")
    public ChatRoomResponse createOrJoinRoom(@PathVariable int productId,
                                     @RequestBody Map<String, String> requestBody) {
        String buyerId = requestBody.get("buyerId");
        if (buyerId == null) {
            throw new RuntimeException("Buyer ID is missing");
        }
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return chatRoomService.createOrChatRoom(productId, buyer);
    }

//    @MessageMapping("/sendMessage/{roomId}")  // Client gửi tới /app/sendMessage/{roomId}
    // Client gửi tới /app/sendMessage/{roomId}
//    @SendTo("/topic/room/{roomId}")  // Tin nhắn được gửi tới /topic/room/{roomId}
@MessageMapping("/sendMessage")
public void sendMessage(@RequestPart ChatMessageRequest messageRequest) throws JsonProcessingException {
    ChatRoom chatRoom = chatRoomRepository.findById(messageRequest.getRoomId())
            .orElseThrow(() -> new RuntimeException("Chat room not found"));

    if (messageRequest.getImages() != null) {
        List<String> base64Images = messageRequest.getImages().stream()
                .map(file -> {
                    byte[] bytes = Base64.getEncoder().encode(file.getBytes());
                    return new String(bytes);
                })
                .collect(Collectors.toList());

        messageRequest.setImages(base64Images);
    }

    String message = chatMessageService.sendMessage(
            chatRoom,
            messageRequest.getSender(),
            messageRequest.getContent(),
            messageRequest.getImages()
    );

    messagingTemplate.convertAndSend("/topic/room/" + messageRequest.getRoomId(), message);
}



    @GetMapping("/room/{buyerId}")
    public List<ChatRoomResponse> getRoom(@PathVariable String buyerId) {
        return chatRoomService.getAllChatRooms(buyerId);
    }

    @GetMapping("/room/content/{chatRoomId}/{buyerId}")
    public List<ChatMessResponse> getChatContent(@PathVariable int chatRoomId, @PathVariable String buyerId) {
        return chatMessageService.getAllMessagesByBuyerId(chatRoomId,buyerId);
    }
    @GetMapping("/room/message/{chatRoomId}")
    public List<ChatMessResponse> getMessageChatOfRoom(@PathVariable int chatRoomId) {
        return chatMessageService.getMessages(chatRoomId);
    }
    @GetMapping("/room/message/room/{userId}")
    public List<ChatMessResponse> getMessageChatByUser(@PathVariable String userId) {
        return chatMessageService.getAllMessagesByUserId(userId);
    }
}
