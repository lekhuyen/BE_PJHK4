package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.ChatRoom;
import fpt.aptech.server_be.entities.NotificationChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationChatRepository extends JpaRepository<NotificationChat, Integer> {
    @Query("select n from NotificationChat n where  n.chatroom =:chatroom")
     NotificationChat findNotificationByChatRom(@Param("chatroom") ChatRoom chatroom);
}
