package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.ChatMessage;
import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomAndSender(ChatRoom chatRoom, User sender);
//    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
