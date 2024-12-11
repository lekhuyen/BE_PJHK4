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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ChatRoom createOrJoinRoom(@PathVariable int productId,
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
    @MessageMapping("/sendMessage")  // Client gửi tới /app/sendMessage/{roomId}
//    @SendTo("/topic/room/{roomId}")  // Tin nhắn được gửi tới /topic/room/{roomId}
    public void sendMessage(@RequestBody ChatMessageRequest messageRequest) throws JsonProcessingException {
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequest.getRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        String message = chatMessageService.sendMessage(chatRoom, messageRequest.getSender(), messageRequest.getContent());

        messagingTemplate.convertAndSend("/topic/room/" + messageRequest.getRoomId(), message); // Tin nhắn được gửi tới /topic/room/{roomId}
    }

    @GetMapping("/room/{buyerId}")
    public List<ChatRoomResponse> getRoom(@PathVariable String buyerId) {
        return chatRoomService.getAllChatRooms(buyerId);
    }

    @GetMapping("/room/content/{chatRoomId}/{buyerId}")
    public List<ChatMessResponse> getChatContent(@PathVariable int chatRoomId, @PathVariable String buyerId) {
        return chatMessageService.getAllMessagesByBuyerId(chatRoomId,buyerId);
    }
}
