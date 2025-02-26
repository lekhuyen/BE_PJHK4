package fpt.aptech.server_be.service;

import fpt.aptech.server_be.dto.response.ChatRoomResponse;
import fpt.aptech.server_be.entities.Auction_Items;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import fpt.aptech.server_be.mapper.ChatRoomMapper;
import fpt.aptech.server_be.repositories.Auction_ItemsRepository;
import fpt.aptech.server_be.repositories.ChatRoomRepository;
import fpt.aptech.server_be.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatRoomService {
    ChatRoomRepository chatRoomRepository;
    Auction_ItemsRepository auctionItemsRepository;
    UserRepository userRepository;

    public ChatRoomResponse createOrChatRoom(int auctionItemId, User buyer) {
        Auction_Items auction_Item = auctionItemsRepository.findById(auctionItemId)
                .orElseThrow(() -> new RuntimeException("Auction Items are not found"));
        ChatRoom chatRoom = chatRoomRepository.findByAcAuctionItemAndBuyerAndSeller(auction_Item, buyer,auction_Item.getUser());

        if (chatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.setAcAuctionItem(auction_Item);
            chatRoom.setBuyer(buyer);
            chatRoom.setSeller(auction_Item.getUser());
             chatRoomRepository.save(chatRoom);
        return ChatRoomMapper.toChatRoomResponse(chatRoom);
        }
        return ChatRoomMapper.toChatRoomResponse(chatRoom);
    }

    public List<ChatRoomResponse> getAllChatRooms(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User is not found"));
        List<ChatRoom> chatRooms = chatRoomRepository.findAllChatByBuyer(user);

        return chatRooms.stream().map(ChatRoomMapper::toChatRoomResponse).collect(Collectors.toList());
    }

    public ChatRoomResponse getRoomById(int roomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);

        return ChatRoomMapper.toChatRoomResponse(chatRoom.get());
    }
}
