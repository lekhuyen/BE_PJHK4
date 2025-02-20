package fpt.aptech.server_be.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chatRoom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    Date date = new Date();

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Auction_Items acAuctionItem;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> message;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;


    @OneToOne(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "notification")
    private NotificationChat notificationChat;

}
