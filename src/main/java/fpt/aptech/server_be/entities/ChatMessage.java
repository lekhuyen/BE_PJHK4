package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chatMessage")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     int id;

     String content;

    @ElementCollection
    @CollectionTable(name = "chat_images", joinColumns = @JoinColumn(name = "images_id"))
    @Column(name = "images_name")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
     List<String> images;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
     User sender;

    @Column(nullable = false)
     Date timestamp;
}
