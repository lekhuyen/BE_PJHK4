package fpt.aptech.server_be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.aptech.server_be.dto.response.ChatMessResponse;
import fpt.aptech.server_be.dto.response.NotificationChatResponse;
import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.NotificationChat;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.mapper.ChatMessageMapper;
import fpt.aptech.server_be.repositories.ChatMessageRepository;

import fpt.aptech.server_be.repositories.ChatRoomRepository;
import fpt.aptech.server_be.repositories.NotificationChatRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {
    ChatMessageRepository chatMessageRepository;
    UserRepository userRepository;
    ChatRoomRepository chatRoomRepository;
    NotificationChatRepository notificationChatRepository;
    Cloudinary cloudinary;
    SimpMessagingTemplate messagingTemplate;

//    public List<ChatMessage> getMessages(ChatRoom chatRoom) {
//        return chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
//    }
    public List<ChatMessResponse> getMessages(int chatRoom) {
        ChatRoom chatRoom1 = chatRoomRepository.findById(chatRoom).get();
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom1);

        return chatMessages.stream().map(ChatMessageMapper::toChatMessageResponse).collect(Collectors.toList());
    }
//send message
//    public String sendMessage(ChatRoom chatRoom, String sender, String content,List<String> images) throws JsonProcessingException {
//        ObjectMapper om = new ObjectMapper();
//        om.registerModule(new JavaTimeModule());
//
//        List<String> fileNames = new ArrayList<>();
//        for (String image : images) {
//            if(image != null && !image.isEmpty()) {
//                byte[] fileName = Base64.getDecoder().decode(image);
//                try {
//                    Map uploadResult = cloudinary.uploader().upload(fileName, ObjectUtils.emptyMap());
//                    String fileUrl = uploadResult.get("url").toString();
//                    fileNames.add(fileUrl);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }
//
//        User user = new User();
//        user.setId(sender);
//
//        ChatMessage message = new ChatMessage();
//        message.setChatRoom(chatRoom);
//        message.setSender(user);
//        message.setContent(content);
//        message.setTimestamp(new Date());
//        message.setImages(fileNames);
//
//
//        chatMessageRepository.save(message);
//
//        ChatMessResponse chatMessResponse = new ChatMessResponse();
//        chatMessResponse.setId(message.getId());
//        chatMessResponse.setRoomId(message.getChatRoom().getId());
//        chatMessResponse.setSenderId(message.getSender().getId());
//        chatMessResponse.setTimestamp(message.getTimestamp());
//        chatMessResponse.setContent(message.getContent());
//        chatMessResponse.setImages(message.getImages());
//
//        return om.writeValueAsString(chatMessResponse);
//    }


    public ChatMessResponse sendMessage(ChatRoom chatRoom, String sender, String content,List<MultipartFile> images) throws JsonProcessingException {

        List<String> fileNames = new ArrayList<>();
        if(images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String fileName = image.getOriginalFilename();

                if(fileName != null && !fileName.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        String fileUrl = uploadResult.get("url").toString();

                        fileNames.add(fileUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("Error uploading image: " + fileName, e);
                    }
                }
            }
        }

        //notification
        int quantityContent = !content.isEmpty() ? 1 : 0;
        int quantityImage = fileNames.size();



        NotificationChat notificationChat = notificationChatRepository.findNotificationByChatRom(chatRoom);
        if (notificationChat != null) {
            if(Objects.equals(notificationChat.getSellerId(), sender)){
                notificationChat.setQuantityBuyer(notificationChat.getQuantityBuyer() + quantityContent + quantityImage);
            } else if (Objects.equals(notificationChat.getBuyerId(), sender)) {
                notificationChat.setQuantitySeller(notificationChat.getQuantitySeller() + quantityContent + quantityImage);
            }

            notificationChatRepository.save(notificationChat);
        } else {
            notificationChat = NotificationChat.builder()
                    .quantityBuyer(quantityContent + quantityImage)
                    .quantitySeller(quantityContent + quantityImage)
                    .buyerId(chatRoom.getBuyer().getId())
                    .sellerId(chatRoom.getSeller().getId())
                    .isRead(false)
                    .chatroom(chatRoom)
                    .build();
            notificationChatRepository.save(notificationChat);
        }


        chatRoom.setNotificationChat(notificationChat);
        chatRoomRepository.save(chatRoom);

        NotificationChatResponse chatResponse = new NotificationChatResponse();
        if (notificationChat != null) {
            chatResponse.setNotiId(notificationChat.getId());
        }
        chatResponse.setQuantitySeller(notificationChat.getQuantitySeller());
        chatResponse.setQuantityBuyer(notificationChat.getQuantityBuyer());
        chatResponse.setBuyerId(notificationChat.getBuyerId());
        chatResponse.setSellerId(notificationChat.getSellerId());
        chatResponse.setRead(notificationChat.isRead());
        chatResponse.setChatroomId(chatRoom.getId());
        messagingTemplate.convertAndSend("/topic/notificationchat", chatResponse);


        User user = new User();
        user.setId(sender);

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(user);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setImages(fileNames);


        chatMessageRepository.save(message);

        ChatMessResponse chatMessResponse = new ChatMessResponse();
        chatMessResponse.setId(message.getId());
        chatMessResponse.setRoomId(message.getChatRoom().getId());
        chatMessResponse.setSenderId(message.getSender().getId());
        chatMessResponse.setTimestamp(message.getTimestamp());
        chatMessResponse.setContent(message.getContent());
        chatMessResponse.setImages(message.getImages() != null ? message.getImages() : null);

        return chatMessResponse;
    }


    public List<ChatMessResponse> getAllMessagesByBuyerId(int chatRoomId,String buyerId) {
        User user = userRepository.findById(buyerId).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomAndSender(chatRoom, user);
        return chatMessages.stream()
                .map(ChatMessageMapper::toChatMessageResponse)
                .collect(Collectors.toList());
    }
    public List<ChatMessResponse> getAllMessagesByUserId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<ChatMessage> chatMessages = chatMessageRepository.findChatMessageBySender(user);

        return chatMessages.stream()
                .map(ChatMessageMapper::toChatMessageResponse)
                .collect(Collectors.toList());
    }

//    xóa notification chat
    public void deleteNotificationChat(int chatRoomId,String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        NotificationChat notificationChat = notificationChatRepository.findNotificationByChatRom(chatRoom);

        if(notificationChat != null) {
            if (!notificationChat.getBuyerId().isEmpty() && Objects.equals(notificationChat.getBuyerId(), userId)) {
                notificationChat.setQuantityBuyer(0);
            }else if(!notificationChat.getSellerId().isEmpty() && Objects.equals(notificationChat.getSellerId(), userId)) {
                notificationChat.setQuantitySeller(0);
            }
            notificationChatRepository.save(notificationChat);
        }
    }

}
