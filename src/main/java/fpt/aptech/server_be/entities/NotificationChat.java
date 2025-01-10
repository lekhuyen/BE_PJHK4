package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notificationchat1")
public class NotificationChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int quantityBuyer;
    String buyerId;
    int quantitySeller;
    String sellerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chatroom_id", referencedColumnName = "id")
    ChatRoom chatroom;

    boolean isRead = false;
}
